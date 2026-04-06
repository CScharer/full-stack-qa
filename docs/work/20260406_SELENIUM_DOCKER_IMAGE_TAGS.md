# Selenium / SeleniArm Docker image tags (optional follow-up)

**Status:** **Pending** — decide whether to pin compose images, keep `:latest`, or change validation policy.

---

## What’s going on

In **Phase 4**, **`scripts/quality/validate-dependency-versions.sh`** scans **`docker-compose.yml`**, **`docker-compose.dev.yml`**, and **`docker-compose.prod.yml`** for **`image:`** lines matching **`selenium`** or **`seleniarm`**.

- **`:latest` tags** → the script prints a **warning** (not an error): it suggests a versioned tag matching **`selenium.version`** in **`pom.xml`** (e.g. **`4.41.0`**).
- **Explicit tags** (e.g. **`:4.41.0`**) → if the tag **does not equal** **`pom.xml`’s `selenium.version`**, the script counts an **error** and exits **non-zero** when combined with other failures. If they **match**, you get a success line for that image.

Warnings alone still yield **exit code 0** for the script (the branch that runs when there are zero errors but one or more warnings). So **local Grid can keep `latest`** and CI/version validation can still pass—you just see noise in the log.

## Your options

1. **Pin images to the Maven Selenium version (recommended for reproducibility)**  
   Replace **`seleniarm/...:latest`** with **`seleniarm/...:4.41.0`** (or whatever **`pom.xml`** uses), consistently across the compose files you care about. **Before committing**, confirm that tag exists on the registry you pull from (SeleniArm tags sometimes differ or lag slightly vs official **`selenium/*`** images—check Docker Hub or your mirror).

2. **Keep `:latest` and live with warnings**  
   No compose edits. The validator reminds you that builds are non-deterministic until the next pull. Good enough if you prefer “always newest SeleniArm” and only need the script to **pass** (exit 0).

3. **Split environments**  
   e.g. **versioned** tags in **`docker-compose.yml`** / **`docker-compose.prod.yml`** for CI and demos, and **`docker-compose.dev.yml`** stays on **`latest`** for developer convenience—accepting warnings from dev-only files, or later narrowing the script (option 4).

4. **Change the validation policy (script change)**  
   Examples: don’t warn on **`latest`** for **`seleniarm/*`** only; treat **`latest`** as informational (no warning counter); or add an allowlist file for image lines to skip. Use this if product policy is “we intentionally use `latest` for SeleniArm” and you want **zero** warnings without pinning.

5. **Use official `selenium/*` images where architecture allows**  
   On **amd64** Linux (typical CI), **`selenium/hub:4.41.0`** / **`selenium/node-chromium:4.41.0`** often track **`pom.xml`** cleanly. **`seleniarm/*`** is mainly for **Apple Silicon** and similar; compose comments in this repo already mention that. You can align **tag strategy** (pin vs latest) per stack without mixing concerns in one file if you document it.

**Files involved today:** compose files under the repo root (see grep for **`image:.*seleniarm`**); validator logic in **`scripts/quality/validate-dependency-versions.sh`** around **Phase 4** (`SELENIUM_VERSION_POM` from **`pom.xml`**).
