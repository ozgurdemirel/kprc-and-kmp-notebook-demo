.PHONY: build clean run test test-verbose help

help:
	@echo "Available targets for app-notebook:"
	@echo "  build       - Build the notebook application"
	@echo "  clean       - Clean build artifacts"
	@echo "  run         - Run the notebook desktop app"
	@echo "  test        - Run all tests"
	@echo "  test-verbose - Run tests with verbose output"
	@echo "  help        - Show this help message"

build:
	../gradlew :app-notebook:build

clean:
	../gradlew :app-notebook:clean

run:
	../gradlew :app-notebook:run

test:
	../gradlew :app-notebook:test

test-verbose:
	../gradlew :app-notebook:test --info 