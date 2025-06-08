.PHONY: build clean help

help:
	@echo "Available targets:"
	@echo "  build  - Build all modules"
	@echo "  clean  - Clean all build artifacts"
	@echo "  help   - Show this help message"

build:
	./gradlew build

clean:
	./gradlew clean 