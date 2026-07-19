/**
 * Normalizes Markdown pipe tables across the repo:
 * - Wraps with <!-- prettier-ignore-start/end --> when missing
 * - Separator rows use | -- | -- |
 * - One space between pipes; empty cells are `| |` not `|  |`
 *
 * Run: node scripts/quality/normalize-md-tables.mjs
 */
import fs from "node:fs";
import path from "node:path";

const START = "<!-- prettier-ignore-start -->";
const END = "<!-- prettier-ignore-end -->";

function walkDir(dir, out = []) {
  for (const ent of fs.readdirSync(dir, { withFileTypes: true })) {
    if (ent.name === "node_modules" || ent.name === ".git") continue;
    const p = path.join(dir, ent.name);
    if (ent.isDirectory()) walkDir(p, out);
    else if (ent.name.endsWith(".md")) out.push(p);
  }
  return out;
}

/** @param {string[]} cells */
function isSeparatorRow(cells) {
  return (
    cells.length > 0 &&
    cells.every((c) => /^:?-{1,}:?$/.test(c) || c === "--" || c === "---")
  );
}

/**
 * Build a pipe row with exactly one space inside empty cells (`| |`, not `|  |`).
 * Non-empty cells are ` ${content} `; separators are ` -- `.
 *
 * @param {string[]} cells
 * @param {boolean} separator
 */
function formatTableRow(cells, separator = false) {
  const segments = cells.map((cell) => {
    if (separator) return " -- ";
    return cell === "" ? " " : ` ${cell} `;
  });
  return `|${segments.join("|")}|`;
}

/** @param {string} line */
function normalizeTableRow(line) {
  if (!/^\s*\|/.test(line)) return line;
  const parts = line.split("|");
  if (parts.length < 3) return line;
  const cells = parts.slice(1, -1).map((c) => c.trim());
  if (cells.length === 0) return line;
  return formatTableRow(cells, isSeparatorRow(cells));
}

function findTables(lines) {
  /** @type {Array<{ start: number; end: number; inFence: boolean }>} */
  const tables = [];
  let fence = false;
  for (let i = 0; i < lines.length; i++) {
    const line = lines[i];
    if (line.trim().startsWith("```")) {
      fence = !fence;
      continue;
    }
    if (fence) continue;
    if (!/^\s*\|/.test(line)) continue;
    const start = i;
    while (i < lines.length && /^\s*\|/.test(lines[i])) i++;
    tables.push({ start, end: i });
    i--;
  }
  return tables;
}

function hasIgnoreAbove(lines, start) {
  let k = start - 1;
  while (k >= 0 && lines[k].trim() === "") k--;
  return k >= 0 && lines[k].trim() === START;
}

function hasIgnoreBelow(lines, end) {
  let m = end;
  while (m < lines.length && lines[m].trim() === "") m++;
  return m < lines.length && lines[m].trim() === END;
}

function normalizeFile(absPath) {
  const raw = fs.readFileSync(absPath, "utf8");
  const nl = raw.includes("\r\n") ? "\r\n" : "\n";
  const lines = raw.split(/\r?\n/);
  const tables = findTables(lines);
  if (tables.length === 0) return false;

  let changed = false;

  for (let t = tables.length - 1; t >= 0; t--) {
    let { start, end } = tables[t];

    if (!hasIgnoreBelow(lines, end)) {
      lines.splice(end, 0, END);
      changed = true;
    }
    if (!hasIgnoreAbove(lines, start)) {
      lines.splice(start, 0, START);
      changed = true;
      start++;
      end++;
    }

    for (let r = start; r < end; r++) {
      const trimmed = lines[r].trim();
      if (trimmed === START || trimmed === END) continue;
      const next = normalizeTableRow(lines[r]);
      if (next !== lines[r]) {
        lines[r] = next;
        changed = true;
      }
    }
  }

  const next = lines.join(nl);
  if (changed && next !== raw) {
    fs.writeFileSync(absPath, next, "utf8");
    return true;
  }
  return false;
}

const roots = ["docs", "scripts", "tests"];
const files = [];
for (const r of roots) {
  if (fs.existsSync(r)) walkDir(r, files);
}
for (const ent of fs.readdirSync(".", { withFileTypes: true })) {
  if (ent.isFile() && ent.name.endsWith(".md")) files.push(ent.name);
}

const unique = [...new Set(files)];
let changed = 0;
for (const f of unique) {
  if (normalizeFile(f)) {
    changed++;
    console.log(f);
  }
}
console.error(`Normalized tables in ${changed} file(s).`);
