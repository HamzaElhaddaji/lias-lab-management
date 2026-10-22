package com.lias.lias_backend;

import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import tools.jackson.databind.json.JsonMapper;
import java.net.URI;
import java.net.http.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment=SpringBootTest.WebEnvironment.RANDOM_PORT,properties={
 "spring.datasource.url=jdbc:h2:mem:workflow;NON_KEYWORDS=YEAR,CONDITION,READ;DB_CLOSE_DELAY=-1",
 "spring.datasource.driver-class-name=org.h2.Driver","spring.datasource.username=sa","spring.datasource.password=",
 "spring.jpa.hibernate.ddl-auto=create-drop","app.seed.enabled=true","app.mail.enabled=false","app.upload.dir=target/test-uploads"
})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class WorkflowTests {
 @LocalServerPort int port;
 final HttpClient http=HttpClient.newHttpClient();final JsonMapper json=JsonMapper.builder().build();
 HttpResponse<byte[]> call(String method,String path,String token,Object payload)throws Exception {
  var b=HttpRequest.newBuilder(URI.create("http://localhost:"+port+path));if(token!=null)b.header("Authorization","Bearer "+token);
  b.header("Content-Type","application/json");b.method(method,payload==null?HttpRequest.BodyPublishers.noBody():HttpRequest.BodyPublishers.ofString(json.writeValueAsString(payload)));
  return http.send(b.build(),HttpResponse.BodyHandlers.ofByteArray());
 }
 @SuppressWarnings("unchecked") Map<String,Object> object(HttpResponse<byte[]> r){return json.readValue(r.body(),Map.class);}
 @SuppressWarnings("unchecked") List<Map<String,Object>> list(HttpResponse<byte[]> r){return json.readValue(r.body(),List.class);}
 String login(String who)throws Exception {var r=call("POST","/api/auth/login",null,Map.of("email",who+"@lias.ma","password","FriendDemo2026!"));status(r,200);return (String)object(r).get("token");}
 void status(HttpResponse<byte[]> r,int expected){assertEquals(expected,r.statusCode(),new String(r.body(),StandardCharsets.UTF_8));}
 long id(HttpResponse<byte[]> r){return ((Number)object(r).get("id")).longValue();}
 HttpResponse<byte[]> upload(String path,String token,Map<String,String> fields,String fileField,String filename,String contentType,String contents)throws Exception {
  String boundary="lias-test-boundary";var body=new StringBuilder();fields.forEach((k,v)->body.append("--"+boundary+"\r\nContent-Disposition: form-data; name=\""+k+"\"\r\n\r\n"+v+"\r\n"));
  body.append("--"+boundary+"\r\nContent-Disposition: form-data; name=\""+fileField+"\"; filename=\""+filename+"\"\r\nContent-Type: "+contentType+"\r\n\r\n"+contents+"\r\n--"+boundary+"--\r\n");
  var b=HttpRequest.newBuilder(URI.create("http://localhost:"+port+path)).header("Content-Type","multipart/form-data; boundary="+boundary);if(token!=null)b.header("Authorization","Bearer "+token);
  return http.send(b.POST(HttpRequest.BodyPublishers.ofString(body.toString())).build(),HttpResponse.BodyHandlers.ofByteArray());
 }
 @Test @Order(1) void accessMatrixAndPublicSite()throws Exception {
  status(call("GET","/api/public/teams",null,null),200);status(call("GET","/api/public/events",null,null),200);status(call("GET","/api/public/publications",null,null),200);
  status(call("GET","/api/documents",null,null),401);
  String doctoral=login("doctorant");status(call("GET","/api/publications",doctoral,null),200);status(call("GET","/api/documents",doctoral,null),403);status(call("GET","/api/meetings",doctoral,null),403);status(call("GET","/api/equipment",doctoral,null),403);
  status(call("GET","/api/admissions",login("admin"),null),403);status(call("GET","/api/admissions",login("director"),null),200);
  status(call("PATCH","/api/archive/mandates/1/restore",login("director"),null),403);
  status(call("POST","/api/auth/register",null,Map.of("email","bypass@lias.ma","password","password123")),400);
 }
 @Test @Order(2) void persistedEventsPublicationsAndRevisionHistory()throws Exception {
  String director=login("director");String member=login("member");
  var event=new HashMap<String,Object>(Map.of("title","Research seminar","type","SEMINAR","startDate",LocalDate.now().toString(),"edition","2026"));
  var created=call("POST","/api/events",director,event);status(created,200);long eventId=id(created);event.put("title","Updated research seminar");status(call("PUT","/api/events/"+eventId,director,event),200);
  var history=call("GET","/api/history/events/"+eventId,director,null);status(history,200);assertTrue(list(history).size()>=2);assertTrue(new String(history.body()).contains("Research seminar"));assertTrue(new String(history.body()).contains("director@lias.ma"));
  status(call("POST","/api/publications",login("doctorant"),Map.of("title","Doctoral article","type","JOURNAL","year",2026,"authors","Doctoral Student","team","ILIAS")),200);
  assertTrue(new String(call("GET","/api/public/publications",null,null).body()).contains("Doctoral article"));
  var profile=call("GET","/api/members/me",member,null);long memberId=id(profile);status(call("PUT","/api/members/me",member,Map.of("biography","Researcher","birthDate","1995-02-03")),200);
  assertTrue(new String(call("GET","/api/members/me",member,null).body()).contains("1995-02-03"));assertFalse(new String(call("GET","/api/members/"+memberId,director,null).body()).contains("1995-02-03"));
 }
 @Test @Order(3) void documentsAreDownloadableAndArchivingRetainsFiles()throws Exception {
  String member=login("member"),admin=login("admin");var r=upload("/api/documents",member,Map.of("type","REPORT"),"file","test.pdf","application/pdf","%PDF-1.4 test-document");status(r,200);long doc=id(r);
  var downloaded=call("GET","/api/documents/"+doc+"/download",member,null);status(downloaded,200);assertTrue(new String(downloaded.body()).startsWith("%PDF"));
  status(call("DELETE","/api/documents/"+doc,admin,null),204);assertFalse(new String(call("GET","/api/documents",member,null).body()).contains("test.pdf"));
  assertTrue(java.nio.file.Files.list(java.nio.file.Path.of("target/test-uploads")).anyMatch(p->p.getFileName().toString().endsWith(".pdf")));
  assertTrue(new String(call("GET","/api/archive/documents",admin,null).body()).contains("test.pdf"));
  status(call("GET","/api/archive/documents/"+doc+"/download",admin,null),200);
  status(call("PATCH","/api/archive/documents/"+doc+"/restore",admin,null),200);
  status(call("GET","/api/documents/"+doc+"/download",member,null),200);
 }
 @Test @Order(4) void applicationApprovalCreatesAccountAndPreservesDecision()throws Exception {
  var fields=new HashMap<String,String>(Map.of("email","applicant@lias.ma","password","Applicant123!","firstName","New","lastName","Researcher","establishment","University","originLaboratory","External","motivation","Research collaboration","memberStatus","ASSOCIATE"));
  status(upload("/api/public/applications",null,fields,"cv","cv.pdf","application/pdf","%PDF-1.4 applicant-cv"),200);
  status(call("POST","/api/auth/login",null,Map.of("email","applicant@lias.ma","password","Applicant123!")),401);
  String director=login("director");var admissions=call("GET","/api/admissions",director,null);status(admissions,200);long application=((Number)list(admissions).getFirst().get("id")).longValue();
  assertFalse(new String(admissions.body()).contains("passwordHash"));status(call("PATCH","/api/admissions/"+application+"/decision",director,Map.of("decision","APPROVED")),200);
  var logged=call("POST","/api/auth/login",null,Map.of("email","applicant@lias.ma","password","Applicant123!"));status(logged,200);String token=(String)object(logged).get("token");status(call("GET","/api/events",token,null),200);status(call("GET","/api/documents",token,null),403);status(call("POST","/api/publications",token,Map.of("title","Unauthorized","year",2026,"type","JOURNAL")),403);
  status(call("PATCH","/api/admissions/"+application+"/decision",director,Map.of("decision","REJECTED")),400);
  var associate=list(call("GET","/api/admin/members",director,null)).stream().filter(m->m.get("email").equals("applicant@lias.ma")).findFirst().orElseThrow();
  status(call("PATCH","/api/admin/members/"+associate.get("userId")+"/publication-rights?allowed=true",director,null),200);
  status(call("POST","/api/publications",token,Map.of("title","Authorized associate publication","year",2026,"type","JOURNAL")),200);
  status(call("GET","/api/meetings",token,null),403);
  var changed=call("PUT","/api/members/me/account",token,Map.of("email","updated-applicant@lias.ma","currentPassword","Applicant123!"));status(changed,200);
  status(call("GET","/api/members/me",(String)object(changed).get("token"),null),200);
  status(call("GET","/api/members/me",token,null),401);
 }
 @Test @Order(5) void inventoryAssignmentsRequestsAndReturns()throws Exception {
  String admin=login("admin"),member=login("member"),director=login("director");long memberId=id(call("GET","/api/members/me",member,null));
  var equipment=call("POST","/api/equipment",admin,Map.of("name","Laptop","serialNumber","TEST-001","quantity",2,"arrivalDate",LocalDate.now().toString(),"condition","NEW"));status(equipment,201);long equipmentId=id(equipment);
  var assigned=call("POST","/api/equipment/assignments",admin,Map.of("memberId",memberId,"equipmentId",equipmentId,"quantity",1));status(assigned,201);assertEquals(1,object(call("GET","/api/equipment/"+equipmentId,member,null)).get("availableQuantity"));
  status(call("PATCH","/api/equipment/assignments/"+id(assigned)+"/return",admin,Map.of()),200);assertEquals(2,object(call("GET","/api/equipment/"+equipmentId,member,null)).get("availableQuantity"));
  assertNotNull(list(call("GET","/api/equipment/assignments",admin,null)).getFirst().get("returnDate"));
  var req=call("POST","/api/equipment/requests",member,Map.of("equipmentName","Monitor","quantityRequested",1,"justification","Research"));status(req,201);status(call("PATCH","/api/equipment/requests/"+id(req)+"/validate",director,Map.of("decision","APPROVED")),200);
 }
 @Test @Order(6) void discussionScopeCollaborationsAndPdfReport()throws Exception {
  String member=login("member"),director=login("director");status(call("POST","/api/discussions",member,Map.of("content","Laboratory update")),200);status(call("POST","/api/discussions",member,Map.of("content","Team update","team","ISDIAC")),200);status(call("GET","/api/discussions?team=ILIAS",member,null),403);
  assertTrue(new String(call("GET","/api/discussions",member,null).body()).contains("Laboratory update"));
  status(call("POST","/api/collaborations",director,Map.of("partner","Partner University","title","Research agreement","startDate",LocalDate.now().toString(),"activities","Joint seminar")),200);
  var pdf=call("GET","/api/report/annual?year=2026",director,null);status(pdf,200);assertTrue(new String(pdf.body(),0,5,StandardCharsets.US_ASCII).equals("%PDF-"));
 }
 @Test @Order(7) void retirementAndExpiredMandatesRevokeExistingTokens()throws Exception {
  String admin=login("admin"),member=login("member"),director=login("director");var all=list(call("GET","/api/admin/members",admin,null));var m=all.stream().filter(r->r.get("email").equals("member@lias.ma")).findFirst().orElseThrow();
  status(call("PATCH","/api/admin/members/"+m.get("userId")+"/status?status=RETIRED",admin,null),200);status(call("GET","/api/members/me",member,null),401);
  var mandates=list(call("GET","/api/mandates",admin,null));long mandate=((Number)mandates.stream().filter(r->r.get("role").equals("DIRECTOR")).findFirst().orElseThrow().get("id")).longValue();
  status(call("PATCH","/api/mandates/"+mandate+"/end",admin,null),200);status(call("GET","/api/admissions",director,null),403);
 }
}
