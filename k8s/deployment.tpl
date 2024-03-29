apiVersion: apps/v1
kind: Deployment
metadata:
  name: car-rental
spec:
  selector:
    matchLabels:
      app: car-rental
  replicas: 2
  template:
    metadata:
      labels:
        app: car-rental
    spec:
      containers:
        - name: car-rental
          image: remoraes/com.tus.microservices.car-rental:{image_tag}
          imagePullPolicy: Always
          ports:
            - containerPort: 8082
---
apiVersion: v1
kind: Service
metadata:
  name: car-rental
spec:
  selector:
    app: car-rental
  ports:
    - name: http
      port: 8082
      targetPort: 8082
  #     nodePort: 30000
  type: LoadBalancer