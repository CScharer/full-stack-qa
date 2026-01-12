import { Page, Locator, expect } from '@playwright/test';
import { BasePage } from './BasePage';

/**
 * Page Object Model for the Notes List Page
 * Uses data-qa selectors consistent with frontend/app/notes/page.tsx
 */
export class NotesPage extends BasePage {
  // Selectors - use data-qa attributes where available
  readonly title: Locator;
  readonly newNoteButton: Locator;
  readonly filtersCard: Locator;
  readonly applicationIdFilter: Locator;
  readonly notesList: Locator;
  readonly emptyState: Locator;
  readonly paginationPreviousButton: Locator;
  readonly paginationNextButton: Locator;

  constructor(page: Page) {
    super(page);
    // Title selector - uses data-qa attribute
    this.title = page.locator('[data-qa="notes-title"]');
    // New note button - NOTE: Notes page does not have a "New Note" button
    // Notes can only be created from within an application page
    // This selector is kept for backward compatibility but will not match any element on the notes page
    this.newNoteButton = page.locator('a[href="/notes/new"], button:has-text("Add")');
    // Filters
    this.filtersCard = page.locator('[data-qa="notes-filters"]');
    this.applicationIdFilter = page.locator('[data-qa="notes-filter-application-id"]');
    // Notes list - uses data-qa attribute
    this.notesList = page.locator('[data-qa="notes-list-card"]');
    // Empty state - uses data-qa attribute
    this.emptyState = page.locator('[data-qa="notes-empty-state"]');
    // Pagination
    this.paginationPreviousButton = page.locator('[data-qa="notes-pagination-previous-button"]');
    this.paginationNextButton = page.locator('[data-qa="notes-pagination-next-button"]');
  }

  /**
   * Navigate to notes page
   */
  async navigate(): Promise<void> {
    await super.navigate('/notes');
  }

  /**
   * Verify page has loaded
   */
  async verifyPageLoaded(): Promise<void> {
    await this.waitForPageLoad();
    await expect(this.title).toContainText('Notes');
    await expect(this.page.locator('body')).toBeVisible();
  }

  /**
   * Click New Note button
   * NOTE: This method will not work on the notes page as there is no "New Note" button.
   * Notes can only be created from within an application page.
   * This method is kept for backward compatibility.
   */
  async clickNewNote(): Promise<void> {
    // This will fail if called - notes page doesn't have a new note button
    await this.newNoteButton.click();
  }

  /**
   * Filter by application ID
   * @param applicationId - Application ID to filter by
   */
  async filterByApplicationId(applicationId: number): Promise<void> {
    await this.applicationIdFilter.fill(applicationId.toString());
  }

  /**
   * Verify notes list is visible
   */
  async verifyListVisible(): Promise<void> {
    await expect(this.notesList.first()).toBeVisible();
  }

  /**
   * Verify empty state is visible
   */
  async verifyEmptyState(): Promise<void> {
    await expect(this.emptyState).toBeVisible();
  }
}
