<p align="center">
  <a href="https://www.kestra.io">
    <img src="https://kestra.io/banner.png"  alt="Kestra workflow orchestrator" />
  </a>
</p>

<h1 align="center" style="border-bottom: none">
    Event-Driven Declarative Orchestrator
</h1>

<div align="center">
 <a href="https://github.com/kestra-io/kestra/releases"><img src="https://img.shields.io/github/tag-pre/kestra-io/kestra.svg?color=blueviolet" alt="Last Version" /></a>
  <a href="https://github.com/kestra-io/kestra/blob/develop/LICENSE"><img src="https://img.shields.io/github/license/kestra-io/kestra?color=blueviolet" alt="License" /></a>
  <a href="https://github.com/kestra-io/kestra/stargazers"><img src="https://img.shields.io/github/stars/kestra-io/kestra?color=blueviolet&logo=github" alt="Github star" /></a> <br>
<a href="https://kestra.io"><img src="https://img.shields.io/badge/Website-kestra.io-192A4E?color=blueviolet" alt="Kestra infinitely scalable orchestration and scheduling platform"></a>
<a href="https://kestra.io/slack"><img src="https://img.shields.io/badge/Slack-Join%20Community-blueviolet?logo=slack" alt="Slack"></a>
</div>

<br />

<p align="center">
    <a href="https://twitter.com/kestra_io"><img height="25" src="https://kestra.io/twitter.svg" alt="twitter" /></a> &nbsp;
    <a href="https://www.linkedin.com/company/kestra/"><img height="25" src="https://kestra.io/linkedin.svg" alt="linkedin" /></a> &nbsp;
<a href="https://www.youtube.com/@kestra-io"><img height="25" src="https://kestra.io/youtube.svg" alt="youtube" /></a> &nbsp;
</p>

<br />
<p align="center">
    <a href="https://go.kestra.io/video/product-overview" target="_blank">
        <img src="https://kestra.io/startvideo.png" alt="Get started in 4 minutes with Kestra" width="640px" />
    </a>
</p>
<p align="center" style="color:grey;"><i>Get started with Kestra in 4 minutes.</i></p>


# Dojo - Kestra plugin

> A coding dojo for a Kestra plugin

## Prerequisites
- Java 21
- Gradle
- Docker
- An IDE

## Installation
- Open the repository in your IDE
- Make sure annotation processing is enabled in your IDE
- Starts the docker compose stack via `docker compose up`

## Build & Test
To build the plugin, do:

```shell
gradle shadowJar
```

Then copy the plugin inside the Kestra container (you can use `docker ps` to find the identifier of the Kestra container):

```shell
docker cp build/libs/dojo-kestra-plugin-0.22.0-SNAPSHOT.jar <containerId>:/app/plugins
```

Finally, restart your docker compose stack so the new plugin will be discovered.

## Exercise 1 - Test the examples

Kestra UI will be available at http://localhost:8080

The plugin already contains an example task and an example trigger, you can have a look at them:
- [Example task](/src/main/java/io/kestra/plugin/dojo/Example.java)
- [Example trigger](/src/main/java/io/kestra/plugin/dojo/Trigger.java)

To try them, in the Kestra UI, go to **Flows** then click **Create*, then paste the following flow YAML inside the editor:

```yaml
id: example
namespace: company.team

triggers:
  - id: trigger
    type: io.kestra.plugin.dojo.Trigger

tasks:
  - id: example
    type: io.kestra.plugin.dojo.Example
    format: "{{flow.id}}"
```

You can execute the flow via the **Execute** button.
The trigger will create an execution each 60s, 50% of times.

## Exercise 2 - Create a new task

Please use the following reference documentation for creating a task: [Plugin Developer Guide - Develop a Task](https://kestra.io/docs/plugin-developer-guide/task).

Let's create a new runnable task `io.kestra.plugin.dojo.FetchDummyJson`.
This task will allow fetching the [Dummy JSON API](https://dummyjson.com) for a type of JSON with a limit (number of documents retrieved), then add the response as a file inside Kestra's internal storage.

The task must have two attributes:
- `jsonType`: a `String` with the type of JSON to fetch (for ex `products`)
- `limit`: an `Integer` with a default of 10

Based on these attributes, the task will compute the URL to fetch, use an HTTP client (Java HttpClient) then store the response inside an internal storage file.

For storing a file, you first need to create it using `runContext.workingDir().createFile()`.
Then to send it to the internal storage you must use `runContext.storage.putFile()`, this method returns and URI that must be used to build an output that will be returned by the task.

You can use the following flow to test it:

```yaml
id: dummy-json
namespace: io.kestra.tests

tasks:
  - id: fetch-products
    type: io.kestra.plugin.dojo.FetchDummyJson
    jsonType: products
    limit: 25
```
