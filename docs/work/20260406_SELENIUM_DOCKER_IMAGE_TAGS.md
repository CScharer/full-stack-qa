# Selenium / SeleniArm Docker image tags

**Status:** **Resolved (2026-09-29)** — local compose files pin official **`selenium/*:4.49.0`** (aligned with **`pom.xml`** / **`env-fe.yml`**).

---

## What changed

Local **`docker-compose.yml`**, **`docker-compose.dev.yml`**, and **`docker-compose.prod.yml`** previously used **`seleniarm/...:latest`** (Apple Silicon workaround). That caused:

- Version-validator **warnings** (`scripts/quality/validate-dependency-versions.sh` Phase 4)
- Non-deterministic Grid versions on every pull
- Drift vs CI, which already uses pinned **`selenium/*:${selenium_version}`**

**SeleniArm does not publish modern tags** such as **`4.49.0`** (registry tops out around older **4.9.x** lines). Official Selenium images **do**, and **`selenium/hub`**, **`node-chrome`**, and **`node-firefox`** publish **amd64 + arm64** for **4.49.0**.

### Resolution

| Role | Image |
| -- | -- |
| Hub | `selenium/hub:4.49.0` |
| Chrome | `selenium/node-chrome:4.49.0` |
| Firefox | `selenium/node-firefox:4.49.0` |
| Edge (local) | `selenium/node-chrome:4.49.0` stand-in — `selenium/node-edge:4.49.0` is **amd64-only**; CI still uses real Edge |

Forced **`platform: linux/arm64`** was removed so Docker selects the native arch.

When bumping Selenium, update **`pom.xml`**, **`env-fe.yml`**, and these three compose tags together.

---

## Historical options (kept for context)

1. Pin to Maven Selenium version — **done** via official images.
2. Keep `:latest` — avoided; non-reproducible.
3. Split env (versioned prod / latest dev) — not needed after official multi-arch pins.
4. Change validation policy — not needed.
5. Prefer **`selenium/*`** over **`seleniarm/*`** — **done**.
