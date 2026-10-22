import { clsx as clsxLib } from 'clsx';
import { twMerge } from 'tailwind-merge';

type ClassValue =
  | string
  | number
  | boolean
  | undefined
  | null
  | ClassValue[];

export function clsx(...classes: ClassValue[]): string {
  return clsxLib(classes);
}

export function cn(...inputs: ClassValue[]) {
  return twMerge(clsx(inputs));
}
