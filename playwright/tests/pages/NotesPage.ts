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
    // Title selector (fallback to h1.h2 if data-qa not available)
    this.title = page.locator('h1.h2:has-text("Notes")');
    // New note button (fallback to button with "Add" text)
    this.newNoteButton = page.locator('a[href="/notes/new"], button:has-text("Add")');
    // Filters
    this.filtersCard = page.locator('[data-qa="notes-filters"]');
    this.applicationIdFilter = page.locator('[data-qa="notes-filter-application-id"]');
    // Notes list (fallback to card or list if data-qa not available)
    this.notesList = page.locator('.card, .list-group');
    // Empty state
    this.emptyState = page.locator('text=No notes found');
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
   */
  async clickNewNote(): Promise<void> {
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
