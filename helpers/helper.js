import net from 'net';

export function findFirstNonRepeatingCharacter(str) {
    const charCounts = new Map();

    for (const char of str) {
        charCounts.set(char, (charCounts.get(char) || 0) + 1);
    }
    for (const char of str) {
        if (charCounts.get(char) === 1) {
            return char;
        }
    }
    return null;
}

export function findPairsThatSumTo(arr, target) {
  const pairs = [];
  const seen = new Set();

  for (const num of arr) {
    const complement = target - num;
    // If the complement is already in the set, a pair is found
    if (seen.has(complement)) {
      pairs.push([complement, num]);
    }
    // Add the current number to the set for future checks
    seen.add(num);
  }
  return pairs;
}


export function isIPv4(ip) {
    return net.isIPv4(ip);
}

export function removeDuplicates(arr) {
  const unique = new Set(arr)
  return [...unique]
}

// TESTS TESTS TESTS TESTS TESTS TESTS TESTS TESTS TESTS TESTS TESTS TESTS TESTS TESTS TESTS TESTS TESTS TESTS TESTS TESTS TESTS TESTS TESTS TESTS TESTS
function findFirstNonRepeatingCharacterTest() {
  const strings = [
    { string: "programming", value: "p" },
    { string: "aabbccddeeff", value: null },
    { string: "teeter", value: "r" },
    { string: "abdcddefabchaafg", value: "e" }
  ]
  console.log("strings", strings)
  strings.forEach(item => {
    console.log(`findFirstNonRepeatingCharacter('${item.string}') Actual: ${findFirstNonRepeatingCharacter(item.string)} = Expected: ${item.value}`);
  })
}

function findPairsThatSumToTest() {
  // Example: arr = [1,2,3,4,5], target = 5 => pairs: [ [1,4], [2,3] ]
  // Example usage:
  const numbers = [1, 2, 4, 3, 5, 7, 8, 9];
  const target = 10;
  const result = findPairsThatSumTo(numbers, target);
  // console.log(`findPairsThatSumTo(${numbers}, ${target}) = ${findPairsThatSumTo(numbers, target)}`); // Output: [[2, 8], [3, 7], [1, 9]]
  console.log("numbers", numbers); 
  console.log("target", target); 
  // console.log(`Pairs that sum to ${target}:`, result); 
  console.log(`findPairsThatSumTo(${numbers}, ${target}) = `, findPairsThatSumTo(numbers, target)); // Output: [[2, 8], [3, 7], [1, 9]]
}

function isIPv4Test() {
  // Example: '192.168.1.1' is valid, '256.100.50.0' is not.
  const methods = [isIPv4];
  const ips = [
    "192.168.1.1", // true
    "not.an.ip",   // false
  ]
  console.log("const net = require('net');")
  console.log("methods", methods)
  console.log("ips", ips)
  methods.forEach((method, _) => {
    console.log(`net.${method.name}()`);
    ips.forEach((ip, _) => {
      console.log(`net.${method.name}('${ip}') = ${method(ip)}`); // ${method}`);
    });
  });
}

function removeDuplicatesTest() {
  // Example usage:
  const numbers = [1,2,2,3,4,4,5];
  const result = removeDuplicates(numbers);
  console.log("numbers", numbers); 
  // console.log(`removeDuplicates(${numbers}) = ${removeDuplicates(numbers)}`); // Output: [1,2,3,4,5]
  console.log(`removeDuplicates(${numbers}) = `, result); // Output: [1,2,3,4,5]
}

// import { defineConfig, devices } from '@playwright/test';
//  export default defineConfig({
//   testDir: './tests',
//   timeout: 30000,
//   retries: 0,
//   use: {
//     baseURL: 'https://demo.playwright.dev',
//     trace: 'on-first-retry',
//   },
//   projects: [
//     {
//       name: 'chromium',
//       use: { ...devices['Desktop Chrome'],channel: 'msedge' }
     
//     }
//   ],
// });

const helperTest = () => {
    findFirstNonRepeatingCharacterTest();
    findPairsThatSumToTest();
    isIPv4Test();
    removeDuplicatesTest();
};

console.clear();
helperTest();