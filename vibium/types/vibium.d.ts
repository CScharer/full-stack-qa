// Type declarations for the vibium package
// Updated for vibium@0.1.2 - the actual released package

declare module 'vibium' {
  interface ActionOptions {
    timeout?: number;
  }

  interface FindOptions {
    timeout?: number;
  }

  interface BoundingBox {
    x: number;
    y: number;
    width: number;
    height: number;
  }

  interface ElementInfo {
    tag: string;
    text: string;
    box: BoundingBox;
  }

  interface LaunchOptions {
    headless?: boolean;
    port?: number;
    executablePath?: string;
  }

  interface LaunchOptionsSync {
    headless?: boolean;
  }

  // Async API - Element
  class Element {
    readonly info: ElementInfo;
    click(options?: ActionOptions): Promise<void>;
    type(text: string, options?: ActionOptions): Promise<void>;
    text(): Promise<string>;
    getAttribute(name: string): Promise<string | null>;
    boundingBox(): Promise<BoundingBox>;
  }

  // Sync API - ElementSync
  class ElementSync {
    readonly info: ElementInfo;
    click(options?: ActionOptions): void;
    type(text: string, options?: ActionOptions): void;
    text(): string;
    getAttribute(name: string): string | null;
    boundingBox(): BoundingBox;
  }

  // Async API - Vibe (browser instance)
  class Vibe {
    go(url: string): Promise<void>;
    screenshot(): Promise<Buffer>;
    evaluate<T = unknown>(script: string): Promise<T>;
    find(selector: string, options?: FindOptions): Promise<Element>;
    quit(): Promise<void>;
  }

  // Sync API - VibeSync (browser instance)
  class VibeSync {
    go(url: string): void;
    screenshot(): Buffer;
    evaluate<T = unknown>(script: string): T;
    find(selector: string, options?: FindOptions): ElementSync;
    quit(): void;
  }

  // Async browser launcher
  export const browser: {
    launch(options?: LaunchOptions): Promise<Vibe>;
  };

  // Sync browser launcher
  export const browserSync: {
    launch(options?: LaunchOptionsSync): VibeSync;
  };

  // Error classes
  export class ConnectionError extends Error {}
  export class TimeoutError extends Error {}
  export class ElementNotFoundError extends Error {
    selector: string;
  }
  export class BrowserCrashedError extends Error {
    exitCode: number;
    output?: string;
  }
}
