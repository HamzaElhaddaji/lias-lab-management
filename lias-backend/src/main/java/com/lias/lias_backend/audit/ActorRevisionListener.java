package com.lias.lias_backend.audit;
import org.hibernate.envers.RevisionListener;
import org.springframework.security.core.context.SecurityContextHolder;
public class ActorRevisionListener implements RevisionListener {
 public void newRevision(Object revision) {
  var auth=SecurityContextHolder.getContext().getAuthentication();
  ((ActorRevision)revision).setActor(auth==null?"system":auth.getName());
 }
}
