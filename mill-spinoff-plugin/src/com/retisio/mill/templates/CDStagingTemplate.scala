package com.retisio.mill.templates

/**
 * Template generator for CD Staging workflow (GitHub Actions).
 *
 * Deploys to staging environment on merge to develop branch.
 *
 * @param serviceName Name of service
 */
class CDStagingTemplate(serviceName: String) {

  def generate(): String = {
    val packageName = serviceName.toLowerCase
    
    s"""name: CD Staging
       |
       |on:
       |  push:
       |    branches: [ develop ]
       |
       |jobs:
       |  deploy-staging:
       |    runs-on: ubuntu-latest
       |    environment: staging
       |    
       |    steps:
       |      - name: Checkout code
       |        uses: actions/checkout@v4
       |      
       |      - name: Set up JDK 21
       |        uses: actions/setup-java@v4
       |        with:
       |          distribution: 'temurin'
       |          java-version: '21'
       |          cache: 'mill'
       |      
       |      - name: Install Mill
       |        run: |
       |          curl -L https://github.com/com-lihaoyi/mill/releases/download/0.11.6/0.11.6 > mill
       |          chmod +x mill
       |          sudo mv mill /usr/local/bin/
       |      
       |      - name: Build JAR
       |        run: mill ${packageName}Service.assembly
       |      
       |      - name: Log in to Docker Hub
       |        uses: docker/login-action@v3
       |        with:
       |          username: $$${{ secrets.DOCKER_USERNAME }}
       |          password: $$${{ secrets.DOCKER_PASSWORD }}
       |      
       |      - name: Build and push Docker image
       |        uses: docker/build-push-action@v5
       |        with:
       |          context: .
       |          push: true
       |          tags: |
       |            retisio/${packageName}-service:staging
       |            retisio/${packageName}-service:staging-$$${{ github.sha }}
       |      
       |      - name: Configure kubectl
       |        uses: azure/k8s-set-context@v3
       |        with:
       |          method: kubeconfig
       |          kubeconfig: $$${{ secrets.KUBECONFIG_STAGING }}
       |      
       |      - name: Deploy to Kubernetes (Staging)
       |        run: |
       |          kubectl set image deployment/${packageName}-service \\
       |            ${packageName}-service=retisio/${packageName}-service:staging-$$${{ github.sha }} \\
       |            -n staging
       |          kubectl rollout status deployment/${packageName}-service -n staging --timeout=5m
       |      
       |      - name: Run smoke tests
       |        run: |
       |          # Wait for deployment to stabilize
       |          sleep 30
       |          
       |          # Run smoke tests against staging
       |          mill ${packageName}Service.itest.test -Dkarate.env=staging -Dkarate.options="--tags @smoke"
       |      
       |      - name: Notify deployment success
       |        if: success()
       |        uses: slackapi/slack-github-action@v1
       |        with:
       |          payload: |
       |            {
       |              "text": "✅ ${serviceName} Service deployed to STAGING",
       |              "blocks": [
       |                {
       |                  "type": "section",
       |                  "text": {
       |                    "type": "mrkdwn",
       |                    "text": "✅ *${serviceName} Service* deployed to *STAGING*\\n*Commit:* $$${{ github.sha }}\\n*Author:* $$${{ github.actor }}"
       |                  }
       |                }
       |              ]
       |            }
       |        env:
       |          SLACK_WEBHOOK_URL: $$${{ secrets.SLACK_WEBHOOK_URL }}
       |      
       |      - name: Notify deployment failure
       |        if: failure()
       |        uses: slackapi/slack-github-action@v1
       |        with:
       |          payload: |
       |            {
       |              "text": "❌ ${serviceName} Service FAILED to deploy to STAGING",
       |              "blocks": [
       |                {
       |                  "type": "section",
       |                  "text": {
       |                    "type": "mrkdwn",
       |                    "text": "❌ *${serviceName} Service* FAILED to deploy to *STAGING*\\n*Commit:* $$${{ github.sha }}\\n*Author:* $$${{ github.actor }}"
       |                  }
       |                }
       |              ]
       |            }
       |        env:
       |          SLACK_WEBHOOK_URL: $$${{ secrets.SLACK_WEBHOOK_URL }}
       |""".stripMargin
  }
}
