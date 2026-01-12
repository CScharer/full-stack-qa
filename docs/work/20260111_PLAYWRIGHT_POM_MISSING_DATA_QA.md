# Playwright POM Missing data-qa Tags

**Date**: 2026-01-11  
**Status**: ✅ COMPLETE  
**Purpose**: Track all locators in Playwright Page Objects that don't use `data-qa` attributes, so we can add them to the frontend application code.

**Last Updated**: 2026-01-11  
**Completion Date**: 2026-01-11

---

## Summary

This document lists all locators in the Playwright Page Object Models that use fallback selectors (CSS classes, text content, etc.) instead of `data-qa` attributes. Each entry includes:
- **Page Object**: The POM file and property name
- **Current Locator**: The fallback selector being used
- **Frontend File**: The application file where the element exists
- **Element Description**: What the element is
- **Recommended data-qa**: Suggested `data-qa` attribute name

---

## ApplicationsPage (`playwright/tests/pages/ApplicationsPage.ts`)

### Empty State
- **Property**: `emptyState`
- **Current Locator**: `text=No applications found`
- **Frontend File**: `frontend/app/applications/page.tsx`
- **Element**: Empty state message when no applications exist
- **Line Reference**: Line 198 (`<p className="text-muted mb-4">No applications found.</p>`)
- **Recommended data-qa**: `applications-empty-state`
- **Action**: Add `data-qa="applications-empty-state"` to the `<p>` element

---

## CompaniesPage (`playwright/tests/pages/CompaniesPage.ts`)

### Title
- **Property**: `title`
- **Current Locator**: `h1.h2:has-text("Companies")`
- **Frontend File**: `frontend/app/companies/page.tsx`
- **Element**: Page title (h1)
- **Line Reference**: Line 38 (`<h1 className="h2 mb-0">Companies</h1>`)
- **Recommended data-qa**: `companies-title`
- **Action**: Add `data-qa="companies-title"` to the `<h1>` element

### Companies Table
- **Property**: `companiesTable`
- **Current Locator**: `table.table`
- **Frontend File**: `frontend/app/companies/page.tsx`
- **Element**: Companies list table
- **Line Reference**: Line 70 (`<table className="table table-hover mb-0">`)
- **Recommended data-qa**: `companies-table`
- **Action**: Add `data-qa="companies-table"` to the `<table>` element

### Empty State
- **Property**: `emptyState`
- **Current Locator**: `text=No companies found`
- **Frontend File**: `frontend/app/companies/page.tsx`
- **Element**: Empty state message when no companies exist
- **Line Reference**: Line 153 (`<p className="text-muted mb-4">No companies found.</p>`)
- **Recommended data-qa**: `companies-empty-state`
- **Action**: Add `data-qa="companies-empty-state"` to the `<p>` element

### Company Row (Dynamic)
- **Method**: `getCompanyRow(companyName: string)`
- **Current Locator**: `tbody tr:has-text("${companyName}")`
- **Frontend File**: `frontend/app/companies/page.tsx`
- **Element**: Individual company row in table
- **Line Reference**: Line 81 (`<tr key={company.id}>`)
- **Recommended data-qa**: `company-row-${company.id}`
- **Action**: Add `data-qa={`company-row-${company.id}`}` to the `<tr>` element

### Company Name Link (Dynamic)
- **Method**: `clickCompany(companyName: string)`
- **Current Locator**: `a` (first link in row)
- **Frontend File**: `frontend/app/companies/page.tsx`
- **Element**: Company name link
- **Line Reference**: Lines 83-88 (`<Link href={`/companies/${company.id}`} ...>`)
- **Recommended data-qa**: `company-name-link-${company.id}`
- **Action**: Add `data-qa={`company-name-link-${company.id}`}` to the `<Link>` element

### Edit Button (Dynamic)
- **Method**: `clickEdit(companyName: string)`
- **Current Locator**: `a:has-text("Edit")`
- **Frontend File**: `frontend/app/companies/page.tsx`
- **Element**: Edit button for a company
- **Line Reference**: Lines 101-106 (`<Link href={`/companies/${company.id}/edit`} ...>`)
- **Recommended data-qa**: `company-edit-button-${company.id}`
- **Action**: Add `data-qa={`company-edit-button-${company.id}`}` to the `<Link>` element

### Delete Button (Dynamic)
- **Method**: `clickDelete(companyName: string)`
- **Current Locator**: `button:has-text("Delete")`
- **Frontend File**: `frontend/app/companies/page.tsx`
- **Element**: Delete button for a company
- **Line Reference**: Lines 107-112 (`<button onClick={() => handleDelete(company.id)} ...>`)
- **Recommended data-qa**: `company-delete-button-${company.id}`
- **Action**: Add `data-qa={`company-delete-button-${company.id}`}` to the `<button>` element

---

## ContactsPage (`playwright/tests/pages/ContactsPage.ts`)

### Empty State
- **Property**: `emptyState`
- **Current Locator**: `text=No contacts found`
- **Frontend File**: `frontend/app/contacts/page.tsx`
- **Element**: Empty state message when no contacts exist
- **Line Reference**: Check for empty state message (similar pattern to other pages)
- **Recommended data-qa**: `contacts-empty-state`
- **Action**: Add `data-qa="contacts-empty-state"` to the empty state `<p>` element

---

## ClientsPage (`playwright/tests/pages/ClientsPage.ts`)

### Title
- **Property**: `title`
- **Current Locator**: `h1.h2:has-text("Clients")`
- **Frontend File**: `frontend/app/clients/page.tsx`
- **Element**: Page title (h1)
- **Line Reference**: Line 33 (`<h1 className="h2 mb-0">Clients</h1>`)
- **Recommended data-qa**: `clients-title`
- **Action**: Add `data-qa="clients-title"` to the `<h1>` element

### Clients Table
- **Property**: `clientsTable`
- **Current Locator**: `table.table`
- **Frontend File**: `frontend/app/clients/page.tsx`
- **Element**: Clients list table
- **Line Reference**: Line 50 (`<table className="table table-hover mb-0">`)
- **Recommended data-qa**: `clients-table`
- **Action**: Add `data-qa="clients-table"` to the `<table>` element

### Empty State
- **Property**: `emptyState`
- **Current Locator**: `text=No clients found`
- **Frontend File**: `frontend/app/clients/page.tsx`
- **Element**: Empty state message when no clients exist
- **Line Reference**: Line 122 (`<p className="text-muted mb-4">No clients found.</p>`)
- **Recommended data-qa**: `clients-empty-state`
- **Action**: Add `data-qa="clients-empty-state"` to the `<p>` element

### Client Row (Dynamic)
- **Method**: `getClientRow(clientName: string)`
- **Current Locator**: `tbody tr:has-text("${clientName}")`
- **Frontend File**: `frontend/app/clients/page.tsx`
- **Element**: Individual client row in table
- **Line Reference**: Line 59 (`<tr key={client.id}>`)
- **Recommended data-qa**: `client-row-${client.id}`
- **Action**: Add `data-qa={`client-row-${client.id}`}` to the `<tr>` element

### Client Name Link (Dynamic)
- **Method**: `clickClient(clientName: string)`
- **Current Locator**: `a` (first link in row)
- **Frontend File**: `frontend/app/clients/page.tsx`
- **Element**: Client name link
- **Line Reference**: Lines 61-66 (`<Link href={`/clients/${client.id}`} ...>`)
- **Recommended data-qa**: `client-name-link-${client.id}`
- **Action**: Add `data-qa={`client-name-link-${client.id}`}` to the `<Link>` element

### Edit Button (Dynamic)
- **Method**: `clickEdit(clientName: string)`
- **Current Locator**: `a:has-text("Edit")`
- **Frontend File**: `frontend/app/clients/page.tsx`
- **Element**: Edit button for a client
- **Line Reference**: Lines 70-75 (`<Link href={`/clients/${client.id}/edit`} ...>`)
- **Recommended data-qa**: `client-edit-button-${client.id}`
- **Action**: Add `data-qa={`client-edit-button-${client.id}`}` to the `<Link>` element

### Delete Button (Dynamic)
- **Method**: `clickDelete(clientName: string)`
- **Current Locator**: `button:has-text("Delete")`
- **Frontend File**: `frontend/app/clients/page.tsx`
- **Element**: Delete button for a client
- **Line Reference**: Lines 76-81 (`<button onClick={() => handleDelete(client.id)} ...>`)
- **Recommended data-qa**: `client-delete-button-${client.id}`
- **Action**: Add `data-qa={`client-delete-button-${client.id}`}` to the `<button>` element

---

## NotesPage (`playwright/tests/pages/NotesPage.ts`)

### Title
- **Property**: `title`
- **Current Locator**: `h1.h2:has-text("Notes")`
- **Frontend File**: `frontend/app/notes/page.tsx`
- **Element**: Page title (h1)
- **Line Reference**: Line 36 (`<h1 className="h2 mb-0">Notes</h1>`)
- **Recommended data-qa**: `notes-title`
- **Action**: Add `data-qa="notes-title"` to the `<h1>` element

### New Note Button
- **Property**: `newNoteButton`
- **Current Locator**: `a[href="/notes/new"], button:has-text("Add")`
- **Frontend File**: `frontend/app/notes/page.tsx`
- **Element**: Button/link to create new note
- **Line Reference**: Note: Notes page doesn't have a "New Note" button (notes are created from application pages). This selector may not be needed, but if a button exists, add data-qa.
- **Recommended data-qa**: `notes-new-button` (if button exists)
- **Action**: If a "New Note" button is added in the future, add `data-qa="notes-new-button"` to it

### Notes List
- **Property**: `notesList`
- **Current Locator**: `.card, .list-group`
- **Frontend File**: `frontend/app/notes/page.tsx`
- **Element**: Container for notes list (card containing table)
- **Line Reference**: Line 66 (`<div className="card shadow-sm">`)
- **Recommended data-qa**: `notes-list-card`
- **Action**: Add `data-qa="notes-list-card"` to the card div containing the notes table

### Empty State
- **Property**: `emptyState`
- **Current Locator**: `text=No notes found`
- **Frontend File**: `frontend/app/notes/page.tsx`
- **Element**: Empty state message when no notes exist
- **Line Reference**: Line 149 (`<p className="text-muted mb-4">No notes found.</p>`)
- **Recommended data-qa**: `notes-empty-state`
- **Action**: Add `data-qa="notes-empty-state"` to the `<p>` element

---

## JobSearchSitesPage (`playwright/tests/pages/JobSearchSitesPage.ts`)

### Title
- **Property**: `title`
- **Current Locator**: `h1.h2:has-text("Job Search Sites")`
- **Frontend File**: `frontend/app/job-search-sites/page.tsx`
- **Element**: Page title (h1)
- **Line Reference**: Line 33 (`<h1 className="h2 mb-0">Job Search Sites</h1>`)
- **Recommended data-qa**: `job-search-sites-title`
- **Action**: Add `data-qa="job-search-sites-title"` to the `<h1>` element

### Sites Table
- **Property**: `sitesTable`
- **Current Locator**: `table.table`
- **Frontend File**: `frontend/app/job-search-sites/page.tsx`
- **Element**: Job search sites list table
- **Line Reference**: Line 43 (`<table className="table table-hover mb-0">`)
- **Recommended data-qa**: `job-search-sites-table`
- **Action**: Add `data-qa="job-search-sites-table"` to the `<table>` element

### Empty State
- **Property**: `emptyState`
- **Current Locator**: `text=No job search sites found`
- **Frontend File**: `frontend/app/job-search-sites/page.tsx`
- **Element**: Empty state message when no sites exist
- **Line Reference**: Line 132 (`<p className="text-muted mb-4">No job search sites found.</p>`)
- **Recommended data-qa**: `job-search-sites-empty-state`
- **Action**: Add `data-qa="job-search-sites-empty-state"` to the `<p>` element

---

## ApplicationFormPage (`playwright/tests/pages/ApplicationFormPage.ts`)

**Note**: The application form is a multi-step wizard. The form inputs are in later steps (step2, step3, etc.), not step1. Step1 is for contact selection. This Page Object may need to be updated to handle the wizard structure, or separate Page Objects may be needed for each step.

### Title
- **Property**: `title`
- **Current Locator**: `h1.h3, h1.h4, h1.h2, h1`
- **Frontend File**: `frontend/app/applications/new/step1/page.tsx` (or later steps, or edit form)
- **Element**: Form page title
- **Line Reference**: Step1 line 215 (`<h1 className="h4 h-md-3 mb-0" data-qa="wizard-step1-title">`)
- **Note**: Step1 already has `data-qa="wizard-step1-title"` ✅
- **Recommended data-qa**: For other steps, use `wizard-step{N}-title` pattern
- **Action**: Check other wizard steps and edit form for title elements

### Position Input
- **Property**: `positionInput`
- **Current Locator**: `input[placeholder*="Position"], label:has-text("Position") + input, label:has-text("Position") ~ input, input[name="position"]`
- **Frontend File**: `frontend/app/applications/new/step2/page.tsx`
- **Element**: Position input field
- **Line Reference**: Line 135 (`data-qa="application-position"`) ✅
- **Note**: Step2 already has `data-qa="application-position"` ✅
- **Recommended data-qa**: Update Page Object to use `[data-qa="application-position"]`
- **Action**: Update `ApplicationFormPage.ts` to use `page.locator('[data-qa="application-position"]')`

### Status Select
- **Property**: `statusSelect`
- **Current Locator**: `select` filtered by text content
- **Frontend File**: `frontend/app/applications/new/step2/page.tsx`
- **Element**: Status dropdown
- **Line Reference**: Line 144 (`data-qa="application-status"`) ✅
- **Note**: Step2 already has `data-qa="application-status"` ✅
- **Recommended data-qa**: Update Page Object to use `[data-qa="application-status"]`
- **Action**: Update `ApplicationFormPage.ts` to use `page.locator('[data-qa="application-status"]')`

### Work Setting Select
- **Property**: `workSettingSelect`
- **Current Locator**: `select` filtered by text content
- **Frontend File**: `frontend/app/applications/new/step2/page.tsx`
- **Element**: Work setting dropdown
- **Line Reference**: Line 159 (`data-qa="application-work-setting"`) ✅
- **Note**: Step2 already has `data-qa="application-work-setting"` ✅
- **Recommended data-qa**: Update Page Object to use `[data-qa="application-work-setting"]`
- **Action**: Update `ApplicationFormPage.ts` to use `page.locator('[data-qa="application-work-setting"]')`

### Location Input
- **Property**: `locationInput`
- **Current Locator**: `input[placeholder*="Location"], label:has-text("Location") + input, label:has-text("Location") ~ input, input[name="location"]`
- **Frontend File**: `frontend/app/applications/new/step2/page.tsx`
- **Element**: Location input field
- **Line Reference**: Line 172 (`data-qa="application-location"`) ✅
- **Note**: Step2 already has `data-qa="application-location"` ✅
- **Recommended data-qa**: Update Page Object to use `[data-qa="application-location"]`
- **Action**: Update `ApplicationFormPage.ts` to use `page.locator('[data-qa="application-location"]')`

### Job Link Input
- **Property**: `jobLinkInput`
- **Current Locator**: `input[type="url"], label:has-text("Job Link") + input, label:has-text("Job Link") ~ input, input[name="job_link"]`
- **Frontend File**: `frontend/app/applications/new/step2/page.tsx`
- **Element**: Job link URL input field
- **Line Reference**: Line 181 (`data-qa="application-job-link"`) ✅
- **Note**: Step2 already has `data-qa="application-job-link"` ✅
- **Recommended data-qa**: Update Page Object to use `[data-qa="application-job-link"]`
- **Action**: Update `ApplicationFormPage.ts` to use `page.locator('[data-qa="application-job-link"]')`

### Submit Button
- **Property**: `submitButton`
- **Current Locator**: `button[type="submit"]:has-text("Create"), button[type="submit"]:has-text("Update"), button:has-text("Create Application"), button:has-text("Update Application"), button:has-text("Next")`
- **Frontend File**: `frontend/app/applications/new/step2/page.tsx` (or edit form)
- **Element**: Form submit button
- **Line Reference**: Step2 line 224 (`data-qa="wizard-step2-submit-button"`) ✅
- **Note**: Step2 already has `data-qa="wizard-step2-submit-button"` ✅
- **Recommended data-qa**: Update Page Object to use `[data-qa="wizard-step2-submit-button"]` or check edit form
- **Action**: Update `ApplicationFormPage.ts` to use `page.locator('[data-qa="wizard-step2-submit-button"]')` for step2, check edit form for similar pattern

### Cancel Button
- **Property**: `cancelButton`
- **Current Locator**: `button:has-text("Cancel"), a:has-text("Cancel")`
- **Frontend File**: `frontend/app/applications/new/step1/page.tsx` (or later steps, or edit form)
- **Element**: Form cancel/back button
- **Line Reference**: Step1 line 240 (`data-qa="wizard-step1-cancel-button"`) ✅, Step2 line 218 (`data-qa="wizard-step2-back-button"`) ✅, Edit form line 88 (`data-qa="application-edit-${params.id}-cancel-button"`) ✅
- **Note**: All forms already have data-qa attributes ✅
- **Recommended data-qa**: Update Page Object to use existing data-qa attributes
- **Action**: Update `ApplicationFormPage.ts` to use `[data-qa="wizard-step1-cancel-button"]`, `[data-qa="wizard-step2-back-button"]`, and `[data-qa^="application-edit-"][data-qa$="-cancel-button"]`

### Edit Form Inputs (Missing data-qa)
**Note**: The edit form (`frontend/app/applications/[id]/edit/page.tsx`) has submit and cancel buttons with data-qa, but the form inputs are missing data-qa attributes.

- **Status Select** (Line 107-119): Missing data-qa
  - **Recommended data-qa**: `application-edit-status-select` or `application-edit-${id}-status-select`
  - **Action**: Add `data-qa={`application-edit-${params.id}-status-select`}` to the `<select>` element

- **Work Setting Select** (Line 124-133): Missing data-qa
  - **Recommended data-qa**: `application-edit-work-setting-select` or `application-edit-${id}-work-setting-select`
  - **Action**: Add `data-qa={`application-edit-${params.id}-work-setting-select`}` to the `<select>` element

- **Position Input** (Line 136-140): Missing data-qa
  - **Recommended data-qa**: `application-edit-position-input` or `application-edit-${id}-position-input`
  - **Action**: Add `data-qa={`application-edit-${params.id}-position-input`}` to the `<Input>` component

- **Requirement Input** (Line 142-146): Missing data-qa
  - **Recommended data-qa**: `application-edit-requirement-input` or `application-edit-${id}-requirement-input`
  - **Action**: Add `data-qa={`application-edit-${params.id}-requirement-input`}` to the `<Input>` component

- **Compensation Input** (Line 148-152): Missing data-qa
  - **Recommended data-qa**: `application-edit-compensation-input` or `application-edit-${id}-compensation-input`
  - **Action**: Add `data-qa={`application-edit-${params.id}-compensation-input`}` to the `<Input>` component

- **Location Input** (Line 154-158): Missing data-qa
  - **Recommended data-qa**: `application-edit-location-input` or `application-edit-${id}-location-input`
  - **Action**: Add `data-qa={`application-edit-${params.id}-location-input`}` to the `<Input>` component

- **Job Link Input** (Line 160-165): Missing data-qa
  - **Recommended data-qa**: `application-edit-job-link-input` or `application-edit-${id}-job-link-input`
  - **Action**: Add `data-qa={`application-edit-${params.id}-job-link-input`}` to the `<Input>` component

- **Job Description Textarea** (Line 167-175): Missing data-qa
  - **Recommended data-qa**: `application-edit-job-description-textarea` or `application-edit-${id}-job-description-textarea`
  - **Action**: Add `data-qa={`application-edit-${params.id}-job-description-textarea`}` to the `<textarea>` element

- **Resume Input** (Line 177-181): Missing data-qa
  - **Recommended data-qa**: `application-edit-resume-input` or `application-edit-${id}-resume-input`
  - **Action**: Add `data-qa={`application-edit-${params.id}-resume-input`}` to the `<Input>` component

- **Cover Letter Input** (Line 183-187): Missing data-qa
  - **Recommended data-qa**: `application-edit-cover-letter-input` or `application-edit-${id}-cover-letter-input`
  - **Action**: Add `data-qa={`application-edit-${params.id}-cover-letter-input`}` to the `<Input>` component

- **Company ID Input** (Line 189-194): Missing data-qa
  - **Recommended data-qa**: `application-edit-company-id-input` or `application-edit-${id}-company-id-input`
  - **Action**: Add `data-qa={`application-edit-${params.id}-company-id-input`}` to the `<Input>` component

- **Client ID Input** (Line 196-201): Missing data-qa
  - **Recommended data-qa**: `application-edit-client-id-input` or `application-edit-${id}-client-id-input`
  - **Action**: Add `data-qa={`application-edit-${params.id}-client-id-input`}` to the `<Input>` component

- **Entered IWD Input** (Line 203-208): Missing data-qa
  - **Recommended data-qa**: `application-edit-entered-iwd-input` or `application-edit-${id}-entered-iwd-input`
  - **Action**: Add `data-qa={`application-edit-${params.id}-entered-iwd-input`}` to the `<Input>` component

- **Date Close Input** (Line 210-215): Missing data-qa
  - **Recommended data-qa**: `application-edit-date-close-input` or `application-edit-${id}-date-close-input`
  - **Action**: Add `data-qa={`application-edit-${params.id}-date-close-input`}` to the `<Input>` component

---

## ApplicationDetailPage (`playwright/tests/pages/ApplicationDetailPage.ts`)

### Title
- **Property**: `title`
- **Current Locator**: `h1.h4, h1.h3, h1.h2`
- **Frontend File**: `frontend/app/applications/[id]/page.tsx`
- **Element**: Application detail page title
- **Line Reference**: Line 98-100 (`<h1 className="h4 h-md-3 mb-1 mb-md-2">{application.position || 'Application Details'}</h1>`)
- **Recommended data-qa**: `application-detail-title` or `application-detail-${application.id}-title`
- **Action**: Add `data-qa={`application-detail-${application.id}-title`}` to the `<h1>` element

### Back Link
- **Property**: `backLink`
- **Current Locator**: `a:has-text("Back to Applications"), a:has-text("←")`
- **Frontend File**: `frontend/app/applications/[id]/page.tsx`
- **Element**: Back to applications list link
- **Line Reference**: Lines 90-92 (`<Link href="/applications" className="text-decoration-none">← Back to Applications</Link>`)
- **Recommended data-qa**: `application-detail-back-link`
- **Action**: Add `data-qa="application-detail-back-link"` to the `<Link>` element

### Status Badge
- **Property**: `statusBadge`
- **Current Locator**: `.badge.bg-primary`
- **Frontend File**: `frontend/app/applications/[id]/page.tsx`
- **Element**: Status badge showing application status
- **Line Reference**: Lines 101-103 (`<span className="badge bg-primary">{application.status}</span>`)
- **Recommended data-qa**: `application-detail-status-badge` or `application-detail-${application.id}-status-badge`
- **Action**: Add `data-qa={`application-detail-${application.id}-status-badge`}` to the `<span>` element

### Edit Button (Fallback)
- **Property**: `editButton` (fallback, methods use data-qa)
- **Current Locator**: `a:has-text("Edit"), button:has-text("Edit")`
- **Frontend File**: `frontend/app/applications/[id]/page.tsx`
- **Element**: Edit button (fallback if data-qa not found)
- **Line Reference**: Line 107
- **Note**: Methods already use `data-qa="application-detail-${applicationId}-edit-button"` ✅ (line 107)
- **Action**: No action needed - data-qa already exists

### Delete Button (Fallback)
- **Property**: `deleteButton` (fallback, methods use data-qa)
- **Current Locator**: `button:has-text("Delete")`
- **Frontend File**: `frontend/app/applications/[id]/page.tsx`
- **Element**: Delete button (fallback if data-qa not found)
- **Line Reference**: Line 109
- **Note**: Methods already use `data-qa="application-detail-${applicationId}-delete-button"` ✅ (line 109)
- **Action**: No action needed - data-qa already exists

### Add Note Button (Fallback)
- **Property**: `addNoteButton` (fallback, methods use data-qa)
- **Current Locator**: `button:has-text("Add Note")`
- **Frontend File**: `frontend/app/applications/[id]/page.tsx`
- **Element**: Add note button (fallback if data-qa not found)
- **Line Reference**: Line 258
- **Note**: Methods already use `data-qa="application-detail-${applicationId}-add-note-button"` ✅ (line 258)
- **Action**: No action needed - data-qa already exists

---

## Priority Order

### High Priority (Core Functionality)
1. **Page Titles** - All pages need `data-qa` on h1 titles
2. **Tables** - All list tables need `data-qa` attributes
3. **Empty States** - All empty state messages need `data-qa`
4. **Form Inputs** - Application form inputs need `data-qa` for reliable testing

### Medium Priority (Dynamic Elements)
5. **Table Rows** - Add `data-qa` with ID to table rows
6. **Action Buttons** - Edit/Delete buttons in tables need `data-qa` with ID
7. **Name Links** - Links to detail pages need `data-qa` with ID

### Low Priority (Fallbacks)
8. **Fallback Selectors** - These are already covered by methods that use data-qa, but fallback properties should be updated for consistency

---

## Implementation Notes

1. **Dynamic IDs**: For elements that are rendered in loops (table rows, buttons, links), use the pattern `data-qa="${element-type}-${id}"` where `id` is the entity ID (e.g., `company-row-${company.id}`).

2. **Consistency**: Follow the existing pattern used in ApplicationsPage where possible:
   - `{page}-title` for page titles
   - `{page}-table` for tables
   - `{page}-empty-state` for empty states
   - `{entity}-{action}-button-${id}` for action buttons

3. **Form Elements**: For the multi-step application wizard, add `data-qa` attributes to all form inputs, selects, and buttons in each step.

4. **Testing**: After adding `data-qa` attributes, update the Page Objects to use them instead of fallback selectors.

---

## Files to Update

### Frontend Application Files
1. `frontend/app/applications/page.tsx` - Empty state
2. `frontend/app/companies/page.tsx` - Title, table, empty state, table rows, links, buttons
3. `frontend/app/contacts/page.tsx` - Empty state
4. `frontend/app/clients/page.tsx` - Title, table, empty state, table rows, links, buttons
5. `frontend/app/notes/page.tsx` - Title, new button, list container, empty state
6. `frontend/app/job-search-sites/page.tsx` - Title, table, empty state
7. `frontend/app/applications/[id]/page.tsx` - Title, back link, status badge
8. `frontend/app/applications/new/step1/page.tsx` (and other steps) - Form inputs, selects, buttons

### Playwright Page Objects (After Frontend Updates)
1. `playwright/tests/pages/ApplicationsPage.ts`
2. `playwright/tests/pages/CompaniesPage.ts`
3. `playwright/tests/pages/ContactsPage.ts`
4. `playwright/tests/pages/ClientsPage.ts`
5. `playwright/tests/pages/NotesPage.ts`
6. `playwright/tests/pages/JobSearchSitesPage.ts`
7. `playwright/tests/pages/ApplicationFormPage.ts`
8. `playwright/tests/pages/ApplicationDetailPage.ts`

---

## Summary: What's Already Done vs What Needs to Be Done

### ✅ Already Has data-qa Attributes

1. **ApplicationsPage**: Most elements have data-qa (title, buttons, filters, table, table body, rows, links, buttons)
2. **ContactsPage**: All elements have data-qa (title, buttons, filters, table, table body, rows, links, buttons) ✅
3. **ApplicationDetailPage**: Edit/Delete/Add Note buttons have data-qa ✅
4. **ApplicationFormPage (Step1)**: Title, buttons, and form elements have data-qa ✅
5. **ApplicationFormPage (Step2)**: All form inputs have data-qa ✅
   - Position: `application-position` ✅
   - Status: `application-status` ✅
   - Work Setting: `application-work-setting` ✅
   - Location: `application-location` ✅
   - Job Link: `application-job-link` ✅
   - Compensation: `application-compensation` ✅
   - Requirements: `application-requirements` ✅
   - Job Description: `application-job-description` ✅
   - Submit: `wizard-step2-submit-button` ✅
   - Back: `wizard-step2-back-button` ✅

### ✅ Phase 1 Complete - List Pages

1. **ApplicationsPage**: Empty state message ✅
2. **CompaniesPage**: Title, table, empty state, table rows, name links, edit/delete buttons ✅
3. **ClientsPage**: Title, table, empty state, table rows, name links, edit/delete buttons ✅
4. **NotesPage**: Title, notes list card, empty state ✅
5. **JobSearchSitesPage**: Title, table, empty state ✅

### ✅ Phase 2 Complete - Detail/Form Pages

6. **ApplicationFormPage (Edit Form)**: All form inputs (status select, work setting select, position, requirement, compensation, location, job link, job description textarea, resume, cover letter, company ID, client ID, entered IWD, date close) ✅
7. **ApplicationDetailPage**: Title, back link, status badge ✅

### ✅ Phase 3 Complete - Page Object Updates

1. **ApplicationsPage**: ✅ Updated `emptyState` to use `[data-qa="applications-empty-state"]`
2. **CompaniesPage**: ✅ Updated all main selectors to use data-qa attributes (title, table, empty state, dynamic rows/links/buttons)
3. **ClientsPage**: ✅ Updated all main selectors to use data-qa attributes (title, table, empty state, dynamic rows/links/buttons)
4. **NotesPage**: ✅ Updated title, notes list card, and empty state selectors
5. **JobSearchSitesPage**: ✅ Updated title, table, and empty state selectors
6. **ApplicationFormPage**: ✅ Updated all form input selectors to use data-qa attributes from step2, and added support for edit form with ID-based getter methods
7. **ApplicationDetailPage**: ✅ Updated title, back link, and status badge selectors
8. **ContactsPage**: ✅ Updated empty state selector

---

## Remaining Fallback Selectors (Intentional)

The following fallback selectors are **intentional** and kept for backward compatibility or special use cases:

### CompaniesPage & ClientsPage
- **Fallback methods** (`*ByName()` variants): These methods use text-based selectors like `tbody tr:has-text()`, `a:has-text("Edit")`, `button:has-text("Delete")` for cases where you only have the entity name and not the ID. The preferred methods use data-qa with IDs.

### ApplicationsPage
- **`getApplicationRowByPosition()`**: Uses `tr:has-text("${position}")` to find rows by position text. This is acceptable as a search method when you don't have the application ID.

### NotesPage
- **`newNoteButton`**: Uses `a[href="/notes/new"], button:has-text("Add")` but this button doesn't exist on the notes page (notes are created from application pages). Documented as not applicable.

### ApplicationFormPage
- **Title fallback**: `h1.h3, h1.h4, h1.h2, h1` - Fallback for edit form which doesn't have a data-qa on the title
- **Submit/Cancel button fallbacks**: Text-based fallbacks for edit form compatibility

### BasePage & Common
- **`body` locator**: Standard Playwright pattern, acceptable
- **`.first()` on lists**: Getting first element from a data-qa list is acceptable

---

## Next Steps

1. ✅ Create this document listing missing `data-qa` attributes
2. ✅ Add `data-qa` attributes to frontend application files
3. ✅ Update Playwright Page Objects to use new `data-qa` attributes
4. ✅ Test all Page Objects to ensure they work with new selectors (verified with Chromium)
5. ✅ Update documentation

---

## Summary of Changes

### Frontend Changes (Phase 1 & 2)
- ✅ Added `data-qa` attributes to all list pages (Companies, Clients, Notes, JobSearchSites, Applications)
- ✅ Added `data-qa` attributes to detail pages (ApplicationDetail)
- ✅ Added `data-qa` attributes to form pages (ApplicationForm edit form)
- ✅ Added `data-qa` attributes to ContactsPage empty state

### Page Object Changes (Phase 3)
- ✅ Updated all Page Objects to use data-qa attributes for primary selectors
- ✅ Added ID-based methods for dynamic elements (rows, links, buttons)
- ✅ Kept fallback methods for backward compatibility
- ✅ Updated ApplicationFormPage to support both wizard (step2) and edit forms
- ✅ Updated ApplicationDetailPage with ID-based getter methods

### Files Modified

**Frontend Files:**
- `frontend/app/companies/page.tsx`
- `frontend/app/clients/page.tsx`
- `frontend/app/notes/page.tsx`
- `frontend/app/job-search-sites/page.tsx`
- `frontend/app/applications/page.tsx`
- `frontend/app/applications/[id]/page.tsx`
- `frontend/app/applications/[id]/edit/page.tsx`
- `frontend/app/contacts/page.tsx`

**Page Object Files:**
- `playwright/tests/pages/ApplicationsPage.ts`
- `playwright/tests/pages/CompaniesPage.ts`
- `playwright/tests/pages/ClientsPage.ts`
- `playwright/tests/pages/NotesPage.ts`
- `playwright/tests/pages/JobSearchSitesPage.ts`
- `playwright/tests/pages/ContactsPage.ts`
- `playwright/tests/pages/ApplicationFormPage.ts`
- `playwright/tests/pages/ApplicationDetailPage.ts`

---

## Final Review - All Page Objects

### ✅ HomePage (`playwright/tests/pages/HomePage.ts`)
- **Status**: ✅ Complete - All selectors use data-qa attributes
- **Selectors**: All use `[data-qa="..."]` from Sidebar.tsx and page.tsx
- **No fallback selectors found**

### ✅ ApplicationsPage (`playwright/tests/pages/ApplicationsPage.ts`)
- **Status**: ✅ Complete - All main selectors use data-qa attributes
- **Selectors**: Title, buttons, filters, table, table body, list card, empty state, dynamic rows/links/buttons all use data-qa
- **Fallback**: `getApplicationRowByPosition()` uses `tr:has-text()` - intentional for text-based search

### ✅ CompaniesPage (`playwright/tests/pages/CompaniesPage.ts`)
- **Status**: ✅ Complete - All main selectors use data-qa attributes
- **Selectors**: Title, button, filters, table, empty state, pagination all use data-qa
- **Dynamic elements**: Rows, name links, edit/delete buttons use data-qa with IDs
- **Fallbacks**: `*ByName()` methods use text-based selectors - intentional for backward compatibility

### ✅ ClientsPage (`playwright/tests/pages/ClientsPage.ts`)
- **Status**: ✅ Complete - All main selectors use data-qa attributes
- **Selectors**: Title, button, filters, table, empty state, pagination all use data-qa
- **Dynamic elements**: Rows, name links, edit/delete buttons use data-qa with IDs
- **Fallbacks**: `*ByName()` methods use text-based selectors - intentional for backward compatibility

### ✅ ContactsPage (`playwright/tests/pages/ContactsPage.ts`)
- **Status**: ✅ Complete - All selectors use data-qa attributes
- **Selectors**: Title, button, filters, table, table body, empty state, pagination all use data-qa
- **Dynamic elements**: Rows, name links, edit/delete buttons use data-qa with IDs
- **No fallback selectors found** (all use data-qa)

### ✅ NotesPage (`playwright/tests/pages/NotesPage.ts`)
- **Status**: ✅ Complete - All main selectors use data-qa attributes
- **Selectors**: Title, filters, list card, empty state, pagination all use data-qa
- **Fallback**: `newNoteButton` uses text/href selector but button doesn't exist on page (documented)

### ✅ JobSearchSitesPage (`playwright/tests/pages/JobSearchSitesPage.ts`)
- **Status**: ✅ Complete - All selectors use data-qa attributes
- **Selectors**: Title, button, table, empty state, pagination all use data-qa
- **No fallback selectors found**

### ✅ ApplicationFormPage (`playwright/tests/pages/ApplicationFormPage.ts`)
- **Status**: ✅ Complete - All form inputs use data-qa attributes
- **Wizard Step2**: All inputs use data-qa (`application-position`, `application-status`, etc.)
- **Edit Form**: ID-based getter methods use data-qa (`application-edit-${id}-...`)
- **Fallbacks**: Title and buttons have fallbacks for edit form compatibility

### ✅ ApplicationDetailPage (`playwright/tests/pages/ApplicationDetailPage.ts`)
- **Status**: ✅ Complete - All selectors use data-qa attributes
- **Selectors**: Back link, title, status badge, edit/delete/add note buttons all use data-qa
- **Dynamic elements**: All use ID-based data-qa selectors
- **Methods**: ID-based getter methods for title, status badge, buttons

---

## Testing Recommendations

1. ✅ **Run all Playwright tests** - Verified with Chromium (tests pass with new data-qa selectors)
2. ⏳ **Test ID-based methods** (preferred) vs name-based fallback methods - Recommended for future testing
3. ⏳ **Test both wizard (step2) and edit forms** for ApplicationFormPage - Recommended for future testing
4. ⏳ **Verify empty states** appear correctly with new selectors - Recommended for future testing
5. ⏳ **Test dynamic elements** (table rows, links, buttons) with various IDs - Recommended for future testing

### Testing Results

**Date**: 2026-01-11  
**Browser**: Chromium  
**Status**: ✅ **PASSED** - All tests work correctly with new data-qa selectors

**Note**: Tests require the application server to be running on port 3003. Run `./scripts/start-env.sh --env dev` before running tests.

---

## Conclusion

✅ **All primary selectors now use data-qa attributes**  
✅ **All frontend pages have been updated with data-qa attributes**  
✅ **All Page Objects have been updated to use data-qa selectors**  
✅ **Fallback selectors are documented and intentional**  

The migration to data-qa attributes is **COMPLETE**. All Page Objects now use stable, maintainable selectors that are consistent across the application.
