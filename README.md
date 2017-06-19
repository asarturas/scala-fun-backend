[![Build Status](https://circleci.com/gh/asarturas/scala-fun-backend/tree/master.svg?style=shield&circle-token=e543cff1b6ea7c6b3b4304a64de92fa6ff159374)](https://circleci.com/gh/asarturas/scala-fun-backend)

## Scala.fun backend

A backend, which meant to feed videos from various sources, process it with a chain of processors and store internally.
It is fed to frontend via API and is meant to be using some sort of prediction engine to suggest relevant next video.
At the moment it's just returning random videos from storage all the time.

## How to run locally?

Make sure you've got scala (at least 2.12.2), sbt (at least 0.13.13) and docker (I have 17.03.1-ce) installed.
Checkout this project and
```
sbt test
docker-compose up -d
sbt run 
```

## Structural components (high level description)

### API Project (api)

This contains API related (backend) stuff.
At the moment it contains all the sourcing and processing as well as storage.
These can potentially be decoupled into their own microservices leaving this as simple API gateway.

#### Sourcers

Gets a feed of videos to process and store:

**Pocket** is the only source currently. It is redacted and not automated.

#### Processors

Once video got from the source, it is processed internally:

**Id** extracts video ID (only supports Youtube and Vimeo);

**Url** composes embed url for video (only supports Youtube and Vimeo).

#### EventStore

Stores feeds of events in the storage:

**RuntimeStorage** is non persistent in memory store;

**GetEventStore** persistent event database from Gregg Young.

### API Client JS Project (api-client-js)

Contains API client, written in Scala.js
It is transpiled into JS and distributed in NPM as suggested API client implementation.

### Backend Project (backend)

Contains data structures, which are shared between API and API Client JS.