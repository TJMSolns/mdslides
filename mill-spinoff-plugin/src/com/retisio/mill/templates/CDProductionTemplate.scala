package com.retisio.mill.templates

/**
 * Template generator for CD Production workflow (GitHub Actions).
 *
 * Deploys to production environment on merge to main branch (with approval).
 *
 * @param serviceName Name of service
 */
class CDProductionTemplate(serviceName: String) {

  def generate(): String = {
    val packageName = serviceName.toLowerCase
    
    s"""name: CD Production
       |
       |on:
       |  push:
       |    branches: [ main ]
       |
       |jobs:
       |  deploy-production:
       |    runs-on: ubuntu-latest
       |    environment: production
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
       |      - name: Extract version
       |        id: version
       |        run: |
       |          VERSION=$$$$(mill show ${packageName}Service.publishVersion | tr -d '"')
       |          echo "version=$$VERSION" >> $$GITHUB_OUTPUT
       |      
       |      - name: Build and push Docker image
       |        uses: docker/build-push-action@v5
       |        with:
       |          context: .
       |          push: true
       |          tags: |
       |            retisio/${packageName}-service:latest
       |            retisio/${packageName}-service:$$${{ steps.version.outputs.version }}
       |            retisio/${packageName}-service:$$${{ github.sha }}
       |      
       |      - name: Configure kubectl
       |        uses: azure/k8s-set-context@v3
       |        with:
       |          method: kubeconfig
       |          kubeconfig: $$${{ secrets.KUBECONFIG_PRODUCTION }}
       |      
       |      - name: Deploy to Kubernetes (Production - Canary)
       |        run: |
       |          # Deploy canary (10% traffic)
       |          kubectl set image deployment/${packageName}-service-canary \\
       |            ${packageName}-service=retisio/${packageName}-service:$$${{ github.sha }} \\
       |            -n production
       |          kubectl rollout status deployment/${packageName}-service-canary -n production --timeout=5m
       |      
       |      - name: Monitor canary deployment
       |        run: |
       |          # Wait for canary to stabilize
       |          sleep 300  # 5 minutes
       |          
       |          # Check canary metrics (error rate, latency)
       |          # This is a placeholder - integrate with your observability platform
       |          echo "Checking canary metrics..."
       |      
       |      - name: Promote to production (full rollout)
       |        run: |
       |          # Deploy to main production deployment (90% traffic)
       |          kubectl set image deployment/${packageName}-service \\
       |            ${packageName}-service=retisio/${packageName}-service:$$${{ github.sha }} \\
       |            -n production
       |          kubectl rollout status deployment/${packageName}-service -n production --timeout=10m
       |      
       |      - name: Run smoke tests
       |        run: |
       |          # Run smoke tests against production
       |          mill ${packageName}Service.itest.test -Dkarate.env=production -Dkarate.options="--tags @smoke"
       |      
       |      - name: Create GitHub Release
       |        uses: actions/create-release@v1
       |        env:
       |          GITHUB_TOKEN: $$${{ secrets.GITHUB_TOKEN }}
       |        with:
       |          tag_name: v$$${{ steps.version.outputs.version }}
       |          release_name: Release v$$${{ steps.version.outputs.version }}
       |          body: |
       |            ## ${serviceName} Service v$$${{ steps.version.outputs.version }}
       |            
       |            Deployed to production on $$${{ github.event.head_commit.timestamp }}
       |            
       |            **Commit:** $$${{ github.sha }}
       |            **Author:** $$${{ github.actor }}
       |            
       |            ### Changes
       |            $$${{ github.event.head_commit.message }}
       |          draft: false
       |          prerelease: false
       |      
       |      - name: Notify deployment success
       |        if: success()
       |        uses: slackapi/slack-github-action@v1
       |        with:
       |          payload: |
       |            {
       |              "text": "🚀 ${serviceName} Service deployed to PRODUCTION",
       |              "blocks": [
       |                {
       |                  "type": "section",
       |                  "text": {
       |                    "type": "mrkdwn",
       |                    "text": "🚀 *${serviceName} Service* deployed to *PRODUCTION*\\n*Version:* v$$${{ steps.version.outputs.version }}\\n*Commit:* $$${{ github.sha }}\\n*Author:* $$${{ github.actor }}"
       |                  }
       |                }
       |              ]
       |            }
       |        env:
       |          SLACK_WEBHOOK_URL: $$${{ secrets.SLACK_WEBHOOK_URL }}
       |      
       |      - name: Rollback on failure
       |        if: failure()
       |        run: |
       |          echo "Rolling back production deployment..."
       |          kubectl rollout undo deployment/${packageName}-service -n production
       |          kubectl rollout undo deployment/${packageName}-service-canary -n production
       |      
       |      - name: Notify deployment failure
       |        if: failure()
       |        uses: slackapi/slack-github-action@v1
       |        with:
       |          payload: |
       |            {
       |              "text": "❌ ${serviceName} Service FAILED to deploy to PRODUCTION (rolled back)",
       |              "blocks": [
       |                {
       |                  "type": "section",
       |                  "text": {
       |                    "type": "mrkdwn",
       |                    "text": "❌ *${serviceName} Service* FAILED to deploy to *PRODUCTION*\\n*Version:* v$$${{ steps.version.outputs.version }}\\n*Commit:* $$${{ github.sha }}\\n*Author:* $$${{ github.actor }}\\n\\n*Deployment has been rolled back.*"
       |                  }
       |                }
       |              ]
       |            }
       |        env:
       |          SLACK_WEBHOOK_URL: $$${{ secrets.SLACK_WEBHOOK_URL }}
       |""".stripMargin
  }
}
