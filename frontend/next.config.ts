import type { NextConfig } from "next";

const nextConfig: NextConfig = {
  /* config options here */
  // Fix workspace root detection warning by explicitly setting turbopack root
  // Note: 'turbo' experimental option removed in Next.js 16.1.0
  // Next.js will automatically detect the correct workspace root
};

export default nextConfig;
