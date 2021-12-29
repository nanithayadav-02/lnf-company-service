@Library('jenkins-shared-library@feature/feature/HRMW-247-create-jenkins-shared-lib-2')_

lnfJavaPipelineWithCD ([repo: 'lnf-company-service', awsAccount: "433686923958", awsRegion: "us-east-1"], {
    return {
        echo '=== Deploying Container Image on EC2 Docker  ==='
        sh 'docker stop lnf-company-service || true && docker rm lnf-company-service || true'
        sh 'docker run -d --name lnf-company-service --network=docker_lnf-app-network -p 8083:8081 -e DATABASE_HOST=postgresdb -e DATABASE_PORT=5432 -e DATABASE_NAME=tsdb -e DATABASE_USERNAME=tsuser -e DATABASE_PASSWORD=ts@12345 -e SPRING_PROFILES_ACTIVE=dev levernfulcrum/lnf-company-service:latest'
    }
})