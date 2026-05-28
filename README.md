# Selenium Jenkins Project

JUnit 4 + Selenium 4 test suite designed to run headlessly on Linux under Jenkins CI, with parallel Firefox and Chrome execution.

---

## Prerequisites

### Required software (Jenkins agent / local machine)
| Tool | Purpose |
|------|---------|
| Java 11+ | Run Maven and tests |
| Maven 3.6+ | Build tool (configured in Jenkins Global Tool Configuration) |
| Firefox | Browser under test |
| Google Chrome | Browser under test |
| geckodriver | WebDriver bridge for Firefox |
| chromedriver | WebDriver bridge for Chrome |
| Xvfb + xvfb-run | Virtual framebuffer for headless display |

Install Xvfb on Debian/Ubuntu:
```bash
sudo apt-get install -y xvfb
```

---

## Installing drivers

Drivers must be placed in `$JENKINS_HOME/selenium-drivers/` (no `.exe` extension).

### 1. Create the directory
```bash
mkdir -p $JENKINS_HOME/selenium-drivers
```

### 2. geckodriver (Firefox)
Find the latest release at https://github.com/mozilla/geckodriver/releases

```bash
VERSION=v0.35.0
wget -O /tmp/geckodriver.tar.gz \
  https://github.com/mozilla/geckodriver/releases/download/${VERSION}/geckodriver-${VERSION}-linux64.tar.gz
tar -xzf /tmp/geckodriver.tar.gz -C $JENKINS_HOME/selenium-drivers/
chmod +x $JENKINS_HOME/selenium-drivers/geckodriver
```

### 3. chromedriver (Chrome / Chromium)
chromedriver version must match the installed Chrome version exactly.

Check your Chrome version first:
```bash
google-chrome --version
# or: chromium-browser --version
```

Then download the matching chromedriver from https://googlechromelabs.github.io/chrome-for-testing/

```bash
# Example for Chrome 125
VERSION=125.0.6422.141
wget -O /tmp/chromedriver.zip \
  https://storage.googleapis.com/chrome-for-testing-public/${VERSION}/linux64/chromedriver-linux64.zip
unzip -j /tmp/chromedriver.zip chromedriver-linux64/chromedriver \
  -d $JENKINS_HOME/selenium-drivers/
chmod +x $JENKINS_HOME/selenium-drivers/chromedriver
```

### 4. Verify
```bash
$JENKINS_HOME/selenium-drivers/geckodriver --version
$JENKINS_HOME/selenium-drivers/chromedriver --version
```

---

## Running locally

Set `DRIVERS_LOC` to point to your local drivers directory before running.

**Firefox (default):**
```bash
export DRIVERS_LOC=/path/to/drivers
mvn clean test -Dbrowser=firefox
```

**Chrome:**
```bash
export DRIVERS_LOC=/path/to/drivers
mvn clean test -Dbrowser=chrome
```

**Both browsers (sequentially):**
```bash
export DRIVERS_LOC=/path/to/drivers
mvn clean test -Dbrowser=firefox -Dsurefire.reportNameSuffix=firefox
mvn test        -Dbrowser=chrome  -Dsurefire.reportNameSuffix=chrome
```

Test reports land in `target/surefire-reports/`.

---

## Running in Jenkins

### Jenkins setup

1. **Required plugins** — install via *Manage Jenkins → Plugins*:
   - Git plugin
   - Pipeline
   - JUnit plugin
   - Workspace Cleanup plugin

2. **Global Tool Configuration** — *Manage Jenkins → Tools*:
   - Add a JDK installation named `JDK11` (or adjust `tools` block in Jenkinsfile)
   - Add a Maven installation named `Maven` (must match the `tools { maven 'Maven' }` name in Jenkinsfile)

3. **Create a Pipeline job**:
   - New Item → Pipeline
   - Under *Pipeline*, select *Pipeline script from SCM*
   - Set your SCM (Git) and repository URL
   - Script Path: `Jenkinsfile`

4. **Install drivers on the agent** as described above, making sure the path is `$JENKINS_HOME/selenium-drivers/`.

5. **Run** — the pipeline will:
   - Check drivers are present and executable
   - Run Firefox and Chrome tests in parallel under Xvfb
   - Publish JUnit XML reports for each browser

---

## Project structure

```
selenium-jenkins-project/
├── pom.xml                          Maven build descriptor
├── Jenkinsfile                      Declarative pipeline
├── .gitignore
├── README.md
└── src/test/java/org/example/selenium/
    ├── BaseTest.java                setUp/tearDown, reads DRIVERS_LOC
    ├── SampleFirefoxTest.java       Firefox smoke test
    └── SampleChromeTest.java        Chrome smoke test
```
