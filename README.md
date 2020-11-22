# devops-exam-app
<a href="https://www.statuscake.com" title="Website Uptime Monitoring"><img src="https://app.statuscake.com/button/index.php?Track=5742658&Days=1&Design=1" /></a>
[![Build Status](https://travis-ci.com/alexander474/devops-exam-app.svg?token=Jcye5ttDhAMRpUM3Ca28&branch=master)](https://travis-ci.com/alexander474/devops-exam-app)

## Setup

```bash
First set Google cloud credentials file. File should 
be placed at project root and be named `terraform.json`

> travis encrypt-file terraform.json --add

```

### Local setup
```
> Start influxDB
> Set spring profile to "local"
> Start App.kt
```