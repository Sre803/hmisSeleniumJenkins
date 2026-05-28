pipeline {
    agent any

    environment {
        DRIVERS_LOC = "$JENKINS_HOME/selenium-drivers"
    }

    tools {
        maven 'Maven'
    }

    stages {

        stage('Git Clone') {
            steps {
                checkout scm
            }
        }

        stage('Check Drivers') {
            steps {
                sh '''
                    echo "Checking driver executables in $DRIVERS_LOC ..."

                    if [ ! -f "$DRIVERS_LOC/geckodriver" ]; then
                        echo "ERROR: geckodriver not found at $DRIVERS_LOC/geckodriver"
                        exit 1
                    fi

                    if [ ! -x "$DRIVERS_LOC/geckodriver" ]; then
                        echo "ERROR: geckodriver is not executable"
                        exit 1
                    fi

                    if [ ! -f "$DRIVERS_LOC/chromedriver" ]; then
                        echo "ERROR: chromedriver not found at $DRIVERS_LOC/chromedriver"
                        exit 1
                    fi

                    if [ ! -x "$DRIVERS_LOC/chromedriver" ]; then
                        echo "ERROR: chromedriver is not executable"
                        exit 1
                    fi

                    echo "geckodriver: OK  ($(\"$DRIVERS_LOC/geckodriver\" --version 2>&1 | head -1))"
                    echo "chromedriver: OK  ($(\"$DRIVERS_LOC/chromedriver\" --version 2>&1 | head -1))"
                '''
            }
        }

        stage('Browser Tests') {
            parallel {

                stage('Firefox Tests') {
                    steps {
                        sh """
                            xvfb-run --auto-servernum --server-args='-screen 0 1920x1080x24' \
                                mvn clean test \
                                    -Dbrowser=firefox \
                                    -Dwebdriver.gecko.driver=${DRIVERS_LOC}/geckodriver \
                                    -Dsurefire.reportNameSuffix=firefox
                        """
                    }
                    post {
                        always {
                            junit testResults: 'target/surefire-reports/*firefox*.xml',
                                  allowEmptyResults: true
                        }
                    }
                }

                stage('Chrome Tests') {
                    steps {
                        sh """
                            xvfb-run --auto-servernum --server-args='-screen 0 1920x1080x24' \
                                mvn clean test \
                                    -Dbrowser=chrome \
                                    -Dwebdriver.chrome.driver=${DRIVERS_LOC}/chromedriver \
                                    -Dsurefire.reportNameSuffix=chrome
                        """
                    }
                    post {
                        always {
                            junit testResults: 'target/surefire-reports/*chrome*.xml',
                                  allowEmptyResults: true
                        }
                    }
                }

            }
        }

    }

    post {
        always {
            cleanWs()
        }
    }
}
