pipeline {
    agent any

    environment {
        DOCKERHUB_USERNAME = 'greatcoderhyd'
        PRODUCT_IMAGE = 'greatcoderhyd/shopping-product'
        SHOPPING_IMAGE = 'greatcoderhyd/shopping-app'
        STACK_NAME = 'shopping'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build and Push Docker Images') {
            steps {
                withCredentials([
                    usernamePassword(
                        credentialsId: 'dockerhub-creds',
                        usernameVariable: 'DH_USER',
                        passwordVariable: 'DH_TOKEN'
                    )
                ]) {
                    sh '''
                        set -eu

                        echo "$DH_TOKEN" | docker login \
                            -u "$DH_USER" --password-stdin

                        docker build \
                            -t "$PRODUCT_IMAGE:latest" \
                            ./product-service

                        docker build \
                            -t "$SHOPPING_IMAGE:latest" \
                            ./shopping-app

                        docker push "$PRODUCT_IMAGE:latest"
                        docker push "$SHOPPING_IMAGE:latest"

                        docker logout
                    '''
                }
            }
        }

        stage('Deploy to Docker Swarm') {
            steps {
                sh '''
                    set -eu

                    docker stack deploy \
                        --with-registry-auth \
                        -c docker-compose.yml \
                        "$STACK_NAME"
                '''
            }
        }

        stage('Verify Deployment') {
            steps {
                sh '''
                    echo "===== SWARM SERVICES ====="
                    docker stack services "$STACK_NAME"

                    echo "===== SERVICE TASKS ====="
                    docker service ps "${STACK_NAME}_product-service"
                    docker service ps "${STACK_NAME}_shopping-app"
                '''
            }
        }
    }
}
