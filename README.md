# Selenium Jenkins Project

Suite de pruebas JUnit 4 + Selenium 4 para ejecución headless en Linux bajo Jenkins CI, con Firefox y Chrome en paralelo.

---

## Requisitos previos

### Software requerido en el agente Jenkins

| Herramienta | Uso |
|-------------|-----|
| Java 11+ | Ejecutar Maven y las pruebas |
| Maven 3.6+ | Gestor de build (configurado en Global Tool Configuration) |
| Firefox | Navegador bajo prueba |
| Google Chrome | Navegador bajo prueba |
| geckodriver | Puente WebDriver para Firefox |
| chromedriver | Puente WebDriver para Chrome |
| Xvfb + xvfb-run | Pantalla virtual para modo headless |

Instalar Xvfb en Debian/Ubuntu:
```bash
sudo apt-get install -y xvfb
```

---

## Instalación de drivers

Los drivers deben estar en `$JENKINS_HOME/selenium-drivers/` (sin extensión `.exe`).

### 1. Crear el directorio
```bash
mkdir -p $JENKINS_HOME/selenium-drivers
```

### 2. geckodriver (Firefox)
Consultar la última versión en https://github.com/mozilla/geckodriver/releases

```bash
VERSION=v0.36.0
wget -O /tmp/geckodriver.tar.gz \
  https://github.com/mozilla/geckodriver/releases/download/${VERSION}/geckodriver-${VERSION}-linux64.tar.gz
tar -xzf /tmp/geckodriver.tar.gz -C $JENKINS_HOME/selenium-drivers/
chmod +x $JENKINS_HOME/selenium-drivers/geckodriver
```

### 3. chromedriver (Chrome)
La versión de chromedriver debe coincidir exactamente con la versión de Chrome instalada.

Verificar la versión de Chrome:
```bash
google-chrome --version
# o: chromium-browser --version
```

Descargar el chromedriver correspondiente desde https://googlechromelabs.github.io/chrome-for-testing/

```bash
# Ejemplo para Chrome 147
VERSION=147.0.7727.137
wget -O /tmp/chromedriver.zip \
  https://storage.googleapis.com/chrome-for-testing-public/${VERSION}/linux64/chromedriver-linux64.zip
unzip -j /tmp/chromedriver.zip chromedriver-linux64/chromedriver \
  -d $JENKINS_HOME/selenium-drivers/
chmod +x $JENKINS_HOME/selenium-drivers/chromedriver
```

### 4. Verificar instalación
```bash
$JENKINS_HOME/selenium-drivers/geckodriver --version
$JENKINS_HOME/selenium-drivers/chromedriver --version
```

---

## Configuración de Jenkins

### 1. Plugins necesarios
Instalar desde *Manage Jenkins → Plugins*:
- Git plugin
- Pipeline
- JUnit plugin
- Workspace Cleanup plugin

### 2. Configuración global de herramientas
En *Manage Jenkins → Tools*:
- Agregar una instalación de Maven con el nombre `Maven` (debe coincidir con `tools { maven 'Maven' }` del Jenkinsfile)
- Agregar una instalación de JDK 11+

### 3. Crear el job Pipeline
1. *New Item → Pipeline*
2. En la sección *Pipeline*, seleccionar *Pipeline script from SCM*
3. Configurar SCM (Git) con la URL del repositorio
4. Branch Specifier: `*/main` (no usar `refs/heads/**`)
5. Script Path: `Jenkinsfile`

### 4. Ejecución
El pipeline ejecuta las siguientes etapas:
1. **Git Clone** — clona el repositorio
2. **Check Drivers** — verifica que geckodriver y chromedriver existen y son ejecutables
3. **Browser Tests** — ejecuta Firefox y Chrome en paralelo bajo Xvfb y publica los reportes JUnit

---

## Estructura del proyecto

```
selenium-jenkins-project/
├── pom.xml                          Descriptor de build Maven
├── Jenkinsfile                      Pipeline declarativo
├── .gitignore
├── README.md
└── src/test/java/org/example/selenium/
    ├── BaseTest.java                setUp/tearDown, lee DRIVERS_LOC
    ├── SampleFirefoxTest.java       Prueba smoke en Firefox
    └── SampleChromeTest.java        Prueba smoke en Chrome
```
