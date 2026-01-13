import type { Metadata } from "next";
import "./globals.css";
import { ReactQueryProvider } from '@/lib/providers/react-query-provider';
import { Sidebar } from '@/components/Sidebar';

export const metadata: Metadata = {
  title: "ONE GOAL - Job Search Application",
  description: "Job search application for tracking applications, companies, contacts, and notes",
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="en" suppressHydrationWarning>
      <body>
        <ReactQueryProvider>
          <div className="d-flex min-vh-100">
            <Sidebar />
            <main className="flex-grow-1" style={{ minWidth: 0 }} data-qa="main-content">
              {children}
            </main>
          </div>
        </ReactQueryProvider>
      </body>
    </html>
  );
}
