/**
 * Date formatting utilities that ensure consistent formatting
 * between server and client to avoid hydration mismatches.
 * 
 * Uses UTC and fixed formatting to ensure identical output
 * regardless of server/client timezone differences.
 */

/**
 * Format a date string or Date object to a consistent locale string.
 * Uses UTC and fixed formatting to ensure server and client render the same.
 */
export function formatDate(date: string | Date): string {
  const dateObj = typeof date === 'string' ? new Date(date) : date;
  
  // Use UTC to avoid timezone differences between server and client
  const year = dateObj.getUTCFullYear();
  const month = dateObj.getUTCMonth();
  const day = dateObj.getUTCDate();
  const hours = dateObj.getUTCHours();
  const minutes = dateObj.getUTCMinutes();
  
  const monthNames = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'];
  const monthName = monthNames[month];
  
  // Format: "Jan 15, 2024, 02:30 PM"
  const hour12 = hours % 12 || 12;
  const ampm = hours < 12 ? 'AM' : 'PM';
  const minutesStr = minutes.toString().padStart(2, '0');
  
  return `${monthName} ${day}, ${year}, ${hour12}:${minutesStr} ${ampm}`;
}

/**
 * Format a date string or Date object to a date-only string.
 * Uses UTC and fixed formatting to ensure server and client render the same.
 */
export function formatDateOnly(date: string | Date): string {
  const dateObj = typeof date === 'string' ? new Date(date) : date;
  
  // Use UTC to avoid timezone differences between server and client
  const year = dateObj.getUTCFullYear();
  const month = dateObj.getUTCMonth();
  const day = dateObj.getUTCDate();
  
  const monthNames = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'];
  const monthName = monthNames[month];
  
  // Format: "Jan 15, 2024"
  return `${monthName} ${day}, ${year}`;
}
