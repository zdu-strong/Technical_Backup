import { onCLS, onINP, onLCP, onFCP, onTTFB } from 'web-vitals';

// If you want to start measuring performance in your app, pass a function
// to log results (for example: onCLS(console.log))
// or send to an analytics endpoint. Learn more: https://bit.ly/CRA-vitals
export function reportWebVitals(isReport?: boolean) {
  if (!isReport) {
    return;
  }
  onCLS(console.log);
  onINP(console.log);
  onLCP(console.log);
  onFCP(console.log);
  onTTFB(console.log);
}
