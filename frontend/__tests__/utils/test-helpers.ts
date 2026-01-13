/**
 * Test helper utilities for querying elements by data-qa attributes
 */
import { screen, within, queries } from '@testing-library/react';
import { queryHelpers } from '@testing-library/dom';

/**
 * Custom query for data-qa attribute
 */
const queryByQa = queryHelpers.queryByAttribute.bind(null, 'data-qa');
const queryAllByQa = queryHelpers.queryAllByAttribute.bind(null, 'data-qa');

/**
 * Query element by data-qa attribute
 * @param qaValue - The value of the data-qa attribute
 * @returns The element with the matching data-qa attribute
 * @throws Error if element is not found
 */
export function getByQa(qaValue: string): HTMLElement {
  const element = queryByQa(document.body, qaValue);
  if (!element) {
    throw new Error(`Unable to find element with data-qa="${qaValue}"`);
  }
  return element;
}

/**
 * Query all elements by data-qa attribute
 * @param qaValue - The value of the data-qa attribute
 * @returns Array of elements with the matching data-qa attribute
 */
export function getAllByQa(qaValue: string): HTMLElement[] {
  return queryAllByQa(document.body, qaValue);
}

/**
 * Query element by data-qa attribute within a container
 * @param container - The container element to search within
 * @param qaValue - The value of the data-qa attribute
 * @returns The element with the matching data-qa attribute
 * @throws Error if element is not found
 */
export function getByQaWithin(container: HTMLElement, qaValue: string): HTMLElement {
  const element = queryByQa(container, qaValue);
  if (!element) {
    throw new Error(`Unable to find element with data-qa="${qaValue}" within container`);
  }
  return element;
}

/**
 * Query all elements by data-qa attribute within a container
 * @param container - The container element to search within
 * @param qaValue - The value of the data-qa attribute
 * @returns Array of elements with the matching data-qa attribute
 */
export function getAllByQaWithin(container: HTMLElement, qaValue: string): HTMLElement[] {
  return queryAllByQa(container, qaValue);
}
