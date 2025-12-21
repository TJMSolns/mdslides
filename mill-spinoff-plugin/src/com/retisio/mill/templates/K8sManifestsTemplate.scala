package com.retisio.mill.templates

/**
 * Template generator for Kubernetes manifests.
 *
 * Generates:
 * - Deployment (with canary variant)
 * - Service (ClusterIP + LoadBalancer)
 * - ConfigMap (application configuration)
 * - HorizontalPodAutoscaler (CPU/memory-based scaling)
 *
 * @param serviceName Name of service
 */
class K8sManifestsTemplate(serviceName: String) {

  private val packageName = serviceName.toLowerCase

  /** Generate Deployment manifest */
  def generateDeployment(): String = {
    s"""apiVersion: apps/v1
       |kind: Deployment
       |metadata:
       |  name: ${packageName}-service
       |  namespace: production
       |  labels:
       |    app: ${packageName}-service
       |    version: stable
       |spec:
       |  replicas: 3
       |  strategy:
       |    type: RollingUpdate
       |    rollingUpdate:
       |      maxSurge: 1
       |      maxUnavailable: 0
       |  selector:
       |    matchLabels:
       |      app: ${packageName}-service
       |      version: stable
       |  template:
       |    metadata:
       |      labels:
       |        app: ${packageName}-service
       |        version: stable
       |      annotations:
       |        prometheus.io/scrape: "true"
       |        prometheus.io/port: "8080"
       |        prometheus.io/path: "/metrics"
       |    spec:
       |      serviceAccountName: ${packageName}-service
       |      containers:
       |      - name: ${packageName}-service
       |        image: retisio/${packageName}-service:latest
       |        imagePullPolicy: Always
       |        ports:
       |        - containerPort: 8080
       |          name: http
       |          protocol: TCP
       |        env:
       |        - name: DATABASE_URL
       |          valueFrom:
       |            secretKeyRef:
       |              name: ${packageName}-secrets
       |              key: database-url
       |        - name: DATABASE_USER
       |          valueFrom:
       |            secretKeyRef:
       |              name: ${packageName}-secrets
       |              key: database-user
       |        - name: DATABASE_PASSWORD
       |          valueFrom:
       |            secretKeyRef:
       |              name: ${packageName}-secrets
       |              key: database-password
       |        - name: KAFKA_BOOTSTRAP_SERVERS
       |          valueFrom:
       |            configMapKeyRef:
       |              name: ${packageName}-config
       |              key: kafka-bootstrap-servers
       |        - name: OTEL_EXPORTER_OTLP_ENDPOINT
       |          valueFrom:
       |            configMapKeyRef:
       |              name: ${packageName}-config
       |              key: otel-endpoint
       |        resources:
       |          requests:
       |            memory: "512Mi"
       |            cpu: "500m"
       |          limits:
       |            memory: "1Gi"
       |            cpu: "1000m"
       |        livenessProbe:
       |          httpGet:
       |            path: /health/live
       |            port: 8080
       |          initialDelaySeconds: 60
       |          periodSeconds: 10
       |          timeoutSeconds: 5
       |          failureThreshold: 3
       |        readinessProbe:
       |          httpGet:
       |            path: /health/ready
       |            port: 8080
       |          initialDelaySeconds: 30
       |          periodSeconds: 5
       |          timeoutSeconds: 3
       |          failureThreshold: 3
       |        securityContext:
       |          allowPrivilegeEscalation: false
       |          runAsNonRoot: true
       |          runAsUser: 1000
       |          capabilities:
       |            drop:
       |            - ALL
       |          readOnlyRootFilesystem: true
       |      affinity:
       |        podAntiAffinity:
       |          preferredDuringSchedulingIgnoredDuringExecution:
       |          - weight: 100
       |            podAffinityTerm:
       |              labelSelector:
       |                matchExpressions:
       |                - key: app
       |                  operator: In
       |                  values:
       |                  - ${packageName}-service
       |              topologyKey: kubernetes.io/hostname
       |---
       |apiVersion: apps/v1
       |kind: Deployment
       |metadata:
       |  name: ${packageName}-service-canary
       |  namespace: production
       |  labels:
       |    app: ${packageName}-service
       |    version: canary
       |spec:
       |  replicas: 1
       |  strategy:
       |    type: RollingUpdate
       |    rollingUpdate:
       |      maxSurge: 1
       |      maxUnavailable: 0
       |  selector:
       |    matchLabels:
       |      app: ${packageName}-service
       |      version: canary
       |  template:
       |    metadata:
       |      labels:
       |        app: ${packageName}-service
       |        version: canary
       |      annotations:
       |        prometheus.io/scrape: "true"
       |        prometheus.io/port: "8080"
       |        prometheus.io/path: "/metrics"
       |    spec:
       |      serviceAccountName: ${packageName}-service
       |      containers:
       |      - name: ${packageName}-service
       |        image: retisio/${packageName}-service:canary
       |        imagePullPolicy: Always
       |        ports:
       |        - containerPort: 8080
       |          name: http
       |          protocol: TCP
       |        env:
       |        - name: DATABASE_URL
       |          valueFrom:
       |            secretKeyRef:
       |              name: ${packageName}-secrets
       |              key: database-url
       |        - name: DATABASE_USER
       |          valueFrom:
       |            secretKeyRef:
       |              name: ${packageName}-secrets
       |              key: database-user
       |        - name: DATABASE_PASSWORD
       |          valueFrom:
       |            secretKeyRef:
       |              name: ${packageName}-secrets
       |              key: database-password
       |        - name: KAFKA_BOOTSTRAP_SERVERS
       |          valueFrom:
       |            configMapKeyRef:
       |              name: ${packageName}-config
       |              key: kafka-bootstrap-servers
       |        - name: OTEL_EXPORTER_OTLP_ENDPOINT
       |          valueFrom:
       |            configMapKeyRef:
       |              name: ${packageName}-config
       |              key: otel-endpoint
       |        resources:
       |          requests:
       |            memory: "512Mi"
       |            cpu: "500m"
       |          limits:
       |            memory: "1Gi"
       |            cpu: "1000m"
       |        livenessProbe:
       |          httpGet:
       |            path: /health/live
       |            port: 8080
       |          initialDelaySeconds: 60
       |          periodSeconds: 10
       |          timeoutSeconds: 5
       |          failureThreshold: 3
       |        readinessProbe:
       |          httpGet:
       |            path: /health/ready
       |            port: 8080
       |          initialDelaySeconds: 30
       |          periodSeconds: 5
       |          timeoutSeconds: 3
       |          failureThreshold: 3
       |        securityContext:
       |          allowPrivilegeEscalation: false
       |          runAsNonRoot: true
       |          runAsUser: 1000
       |          capabilities:
       |            drop:
       |            - ALL
       |          readOnlyRootFilesystem: true
       |""".stripMargin
  }

  /** Generate Service manifest */
  def generateService(): String = {
    s"""apiVersion: v1
       |kind: Service
       |metadata:
       |  name: ${packageName}-service
       |  namespace: production
       |  labels:
       |    app: ${packageName}-service
       |spec:
       |  type: ClusterIP
       |  selector:
       |    app: ${packageName}-service
       |  ports:
       |  - name: http
       |    port: 80
       |    targetPort: 8080
       |    protocol: TCP
       |  sessionAffinity: None
       |---
       |apiVersion: v1
       |kind: Service
       |metadata:
       |  name: ${packageName}-service-lb
       |  namespace: production
       |  labels:
       |    app: ${packageName}-service
       |spec:
       |  type: LoadBalancer
       |  selector:
       |    app: ${packageName}-service
       |  ports:
       |  - name: http
       |    port: 80
       |    targetPort: 8080
       |    protocol: TCP
       |  sessionAffinity: None
       |""".stripMargin
  }

  /** Generate ConfigMap manifest */
  def generateConfigMap(): String = {
    s"""apiVersion: v1
       |kind: ConfigMap
       |metadata:
       |  name: ${packageName}-config
       |  namespace: production
       |data:
       |  kafka-bootstrap-servers: "kafka-broker-1:9092,kafka-broker-2:9092,kafka-broker-3:9092"
       |  otel-endpoint: "http://otel-collector:4317"
       |  log-level: "INFO"
       |  environment: "production"
       |""".stripMargin
  }

  /** Generate HorizontalPodAutoscaler manifest */
  def generateHPA(): String = {
    s"""apiVersion: autoscaling/v2
       |kind: HorizontalPodAutoscaler
       |metadata:
       |  name: ${packageName}-service-hpa
       |  namespace: production
       |spec:
       |  scaleTargetRef:
       |    apiVersion: apps/v1
       |    kind: Deployment
       |    name: ${packageName}-service
       |  minReplicas: 3
       |  maxReplicas: 10
       |  metrics:
       |  - type: Resource
       |    resource:
       |      name: cpu
       |      target:
       |        type: Utilization
       |        averageUtilization: 70
       |  - type: Resource
       |    resource:
       |      name: memory
       |      target:
       |        type: Utilization
       |        averageUtilization: 80
       |  behavior:
       |    scaleDown:
       |      stabilizationWindowSeconds: 300
       |      policies:
       |      - type: Percent
       |        value: 50
       |        periodSeconds: 60
       |    scaleUp:
       |      stabilizationWindowSeconds: 0
       |      policies:
       |      - type: Percent
       |        value: 100
       |        periodSeconds: 30
       |      - type: Pods
       |        value: 2
       |        periodSeconds: 30
       |      selectPolicy: Max
       |""".stripMargin
  }
}
