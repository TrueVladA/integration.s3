{{/*
Expand the name of the chart.
*/}}
{{- define "sbiS3.name" -}}
{{- default .Chart.Name .Values.nameOverride | trunc 63 | trimSuffix "-" }}
{{- end }}

{{/*
Common app container name. 
!! Must have list args on include. (list $ . "string-you-wanna-pass")
*/}}
{{- define "sbiS3.containerName" }}
  {{- $arg := index . 1 | toString }}
  {{- with index . 0 }}
    {{- printf "%s-%s" .Chart.Name $arg }}
  {{- end }}
{{- end }}

{{/*
Common app service resources name. 
!! Must have list args on include. (list $ . "string-you-wanna-pass")
*/}}
{{- define "sbiS3.resourceFullname" }}
  {{- $arg := index . 1 | toString }}
  {{- with index . 0 }}
    {{- printf "%s-%s" (include "sbiS3.fullname" .) $arg | trunc 63 | trimSuffix "-" }}
  {{- end }}
{{- end }}

{{/*
Create a default fully qualified app name.
We truncate at 63 chars because some Kubernetes name fields are limited to this (by the DNS naming spec).
If release name contains chart name it will be used as a full name.
*/}}
{{- define "sbiS3.fullname" -}}
{{- if .Values.fullnameOverride }}
{{- .Values.fullnameOverride | trunc 63 | trimSuffix "-" }}
{{- else }}
{{- $name := default .Chart.Name .Values.nameOverride }}
{{- if contains $name .Release.Name }}
{{- .Release.Name | trunc 63 | trimSuffix "-" }}
{{- else }}
{{- printf "%s-%s" .Release.Name $name | trunc 63 | trimSuffix "-" }}
{{- end }}
{{- end }}
{{- end }}

{{/*
Create chart name and version as used by the chart label.
*/}}
{{- define "sbiS3.chart" -}}
{{- printf "%s-%s" .Chart.Name .Chart.Version | replace "+" "_" | trunc 63 | trimSuffix "-" }}
{{- end }}

{{/*
Common labels
*/}}
{{- define "sbiS3.labels" -}}
helm.sh/chart: {{ include "sbiS3.chart" . }}
{{- if .Chart.AppVersion }}
app.kubernetes.io/version: {{ .Chart.AppVersion | quote }}
{{- end }}
app.kubernetes.io/managed-by: {{ .Release.Service }}
{{- end }}

{{/*
Selector labels
*/}}
{{- define "sbiS3.selectorLabels" -}}
app.kubernetes.io/name: {{ include "sbiS3.name" . }}
app.kubernetes.io/instance: {{ .Release.Name }}
{{- end }}

{{/*
Selector app unic labels
*/}}
{{- define "sbiS3.selectorAppLabels" -}}
{{- $arg := index . 1 | toString }}
{{- with index . 0 }}
app: {{ printf "%s" (include "sbiS3.resourceFullname" (list . $arg)) }}
{{- end }}
{{- end }}

{{/*
Unic selector label for app & service 
!! Must have list args in include. (list . "string-you-wanna-pass")
*/}}
{{- define "sbiS3.deploymentLabels" }}
{{- $arg := index . 1 | toString }}
{{- with index . 0 }}
{{- include "sbiS3.selectorLabels" . }}
{{ include "sbiS3.selectorAppLabels" (list . $arg) }}
{{- end }}
{{- end }}

{{/*
Common app image name for deployments
*/}}
{{- define "sbiS3.image" -}}
{{- printf "%s:%s" .Values.image.name (.Values.image.tag | default "latest") }}
{{- end }}

{{/*
Common app image pullPolicy for deployments
*/}}
{{- define "sbiS3.imagePullPolicy" -}}
{{- .Values.image.pullPolicy | default "IfNotPresent" }}
{{- end }}

{{/*
Common app imagePullSecrets for deployments
*/}}
{{- define "sbiS3.imagePullSecrets" -}}
{{- with .Values.image.imagePullSecrets }}
imagePullSecrets:
{{- toYaml . | nindent 2 }}
{{- end }}
{{- end }}

{{/*
Common vault inject template
*/}}
{{- define "sbiS3.vaultInject" -}}
{{- if .Values.env.vault.enabled }}
vault.hashicorp.com/agent-inject: "true"
vault.hashicorp.com/role: {{ .Values.env.vault.role | squote }}
vault.hashicorp.com/agent-inject-secret-env: {{ .Values.env.vault.kv.path | squote }}
{{- if eq (.Values.env.vault.kv.version | int) 2 }}
vault.hashicorp.com/agent-inject-template-env: |
  {{`{{- with secret `}} {{ .Values.env.vault.kv.path | quote }}{{` -}}`}}
    {{`{{- range $k, $v := .Data.data }}`}}
      {{`export {{ $k }}='{{ $v }}'`}}
    {{`{{- end }}`}}
  {{`{{- end }}`}}
{{- else if eq (.Values.env.vault.kv.version | int) 1 }}
vault.hashicorp.com/agent-inject-template-env: |
  {{`{{- with secret `}} {{ .Values.env.vault.kv.path | quote }}{{` -}}`}}
    {{`{{- range $k, $v := .Data }}`}}
      {{`export {{ $k }}='{{ $v }}'`}}
    {{`{{- end }}`}}
  {{`{{- end }}`}}
{{- end }}
{{- end }}
{{- end }}

{{/*
Common pod annotations template
*/}}
{{- define "sbiS3.podAnnotations" -}}
    {{- include "sbiS3.vaultInject" (index . 0) }}
  {{- with (index . 1) }}
    {{- toYaml . | nindent 0 }}
  {{- end }}
{{- end }}
