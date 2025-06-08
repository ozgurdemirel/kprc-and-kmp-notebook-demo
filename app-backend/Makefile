.PHONY: build clean run test help

help:
	@echo "Available targets for app-backend:"
	@echo "  build  - Build the backend application"
	@echo "  clean  - Clean build artifacts"
	@echo "  run    - Run the backend server"
	@echo "  test   - Run tests"
	@echo "  help   - Show this help message"

build:
	../gradlew :app-backend:build

clean:
	../gradlew :app-backend:clean

run:
	../gradlew :app-backend:run

test:
	../gradlew :app-backend:test 