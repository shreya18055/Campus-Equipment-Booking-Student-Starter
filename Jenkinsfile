pipeline {
    agent any

    environment {
        DOCKER = 'C:\\Users\\SNUC\\AppData\\Local\\Programs\\DockerDesktop\\resources\\bin\\docker.exe'
        DOCKER_DIR = 'C:\\Users\\SNUC\\AppData\\Local\\Programs\\DockerDesktop\\resources\\bin'
        MINIKUBE_HOME = 'C:\\Users\\SNUC\\.minikube'
        KUBECONFIG = 'C:\\Users\\SNUC\\.kube\\config'
        MINIKUBE_PROFILE = 'minikube'
        REPO_URL = 'https://github.com/shreya18055/Campus-Equipment-Booking-Student-Starter.git'
        GIT_BRANCH = 'feature/sequential-booking-inventory'
        IMAGE_NAME = 'campus-equipment-booking:set-g'
        DEPLOYMENT_NAME = 'campus-equipment-booking'
        SERVICE_NAME = 'campus-equipment-booking'
    }

    stages {
        stage('Checkout') {
            steps {
                deleteDir()
                bat 'git -c http.version=HTTP/1.1 clone --depth 1 --branch "%GIT_BRANCH%" "%REPO_URL%" .'
                bat 'git log -1 --oneline'
            }
        }

        stage('Verify Tools') {
            steps {
                withEnv(["PATH+DOCKER=${env.DOCKER_DIR}"]) {
                    bat 'git --version'
                    bat 'mvn -version'
                    bat 'docker --version'
                    bat 'docker info'
                    bat 'minikube version'
                    bat 'minikube profile list'
                }
            }
        }

        stage('Verify Kubernetes Access') {
            steps {
                withEnv(["PATH+DOCKER=${env.DOCKER_DIR}"]) {
                    bat 'minikube status -p minikube'
                    bat 'minikube kubectl -- get nodes'
                }
            }
        }

        stage('Build and Test') {
            steps {
                bat 'mvn clean test'
            }
        }

        stage('Package Application') {
            steps {
                bat 'mvn package -DskipTests'
            }
        }

        stage('Build Docker Image') {
            steps {
                bat '"%DOCKER%" build -t %IMAGE_NAME% .'
            }
        }

        stage('Load Image into Minikube') {
            steps {
                withEnv(["PATH+DOCKER=${env.DOCKER_DIR}"]) {
                    bat 'minikube image load %IMAGE_NAME% -p minikube'
                }
            }
        }

        stage('Deploy to Kubernetes') {
            steps {
                withEnv(["PATH+DOCKER=${env.DOCKER_DIR}"]) {
                    bat 'minikube kubectl -- apply -f k8s/deployment.yaml'
                    bat 'minikube kubectl -- apply -f k8s/service.yaml'
                    bat 'minikube kubectl -- rollout status deployment/%DEPLOYMENT_NAME% --timeout=120s'
                }
            }
        }

        stage('Verify Deployment') {
            steps {
                withEnv(["PATH+DOCKER=${env.DOCKER_DIR}"]) {
                    bat 'minikube kubectl -- get deployments'
                    bat 'minikube kubectl -- get pods'
                    bat 'minikube kubectl -- get services'
                    bat 'minikube kubectl -- get endpoints %SERVICE_NAME%'
                }
            }
        }
    }

    post {
        success {
            echo 'SUCCESS: Tests, Docker build, and Kubernetes deployment completed.'
        }
        failure {
            echo 'FAILURE: Check Console Output for the first failed stage.'
        }
        always {
            echo 'Jenkins pipeline execution finished.'
        }
    }
}
