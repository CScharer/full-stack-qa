import type { NextConfig } from "next";
import path from "path";

const nextConfig: NextConfig = {
  /* config options here */
  // Fix workspace root detection warning by explicitly setting turbopack root
  // This prevents Next.js from detecting the parent directory's package-lock.json
  turbopack: {
    root: path.join(__dirname),
  },
};

export default nextConfig;
