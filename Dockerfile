version: '3.8'

services:
  app:
    build:
      context: .
      dockerfile: Dockerfile
    ports:
      - "8000:8000"
    environment:
      - JAVA_OPTS=-Dspring.profiles.active=prod
    networks:
      - app-network

networks:
  app-network:
    driver: bridge
