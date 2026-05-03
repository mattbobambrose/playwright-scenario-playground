VERSION=$(shell grep '^version=' gradle.properties | head -1 | cut -d= -f2 | sed 's/-SNAPSHOT//')
PLATFORMS=linux/amd64,linux/arm64
IMAGE_NAME=mattbobambrose/playwright-scenario-playground

.PHONY: default clean build tests versioncheck docker-local docker-push docker-run depends run ts-site upgrade-wrapper

default: versioncheck

clean:
	./gradlew clean

build:
	./gradlew build -x test

tests:
	./gradlew test

versioncheck:
	./gradlew dependencyUpdates

docker-local: build
	docker buildx build --load -t $(IMAGE_NAME):latest -t $(IMAGE_NAME):$(VERSION) .

docker-push: build
	docker buildx build --platform $(PLATFORMS) --push -t $(IMAGE_NAME):latest -t $(IMAGE_NAME):$(VERSION) .

docker-run:
	docker run --rm -p 8080:8080 $(IMAGE_NAME):latest

depends:
	./gradlew dependencies

run:
	./gradlew run

ts-site:
	cd typescript && npm start

upgrade-wrapper:
	./gradlew wrapper --gradle-version=9.5.0 --distribution-type=bin
