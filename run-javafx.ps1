# Run CivicTrack JavaFX. Maven is often not on PATH; this script finds JDK 21 + Maven.
$ErrorActionPreference = "Stop"
Set-Location $PSScriptRoot

$localDbEnv = Join-Path $PSScriptRoot "local-db-env.ps1"
if (Test-Path $localDbEnv) {
    . $localDbEnv
    Write-Host "Loaded database env from local-db-env.ps1"
}

if (-not $env:JAVA_HOME -or -not (Test-Path "$env:JAVA_HOME\bin\java.exe")) {
    $jdk = Get-ChildItem "C:\Program Files\Eclipse Adoptium\jdk-21*" -Directory -ErrorAction SilentlyContinue |
        Sort-Object { $_.Name } -Descending |
        Select-Object -First 1
    if ($jdk) {
        $env:JAVA_HOME = $jdk.FullName
    }
}

if (-not $env:JAVA_HOME -or -not (Test-Path "$env:JAVA_HOME\bin\java.exe")) {
    Write-Error "JAVA_HOME not set and JDK 21 not found under 'C:\Program Files\Eclipse Adoptium\jdk-21*'. Install Temurin 21 or set JAVA_HOME."
    exit 1
}

$mvn = $null
if ($env:MAVEN_HOME -and (Test-Path "$env:MAVEN_HOME\bin\mvn.cmd")) {
    $mvn = "$env:MAVEN_HOME\bin\mvn.cmd"
}
elseif (Test-Path "C:\tools\apache-maven-3.9.9\bin\mvn.cmd") {
    $mvn = "C:\tools\apache-maven-3.9.9\bin\mvn.cmd"
}
elseif (Get-Command mvn -ErrorAction SilentlyContinue) {
    $mvn = "mvn"
}

if (-not $mvn) {
    Write-Error "Maven not found. Install Maven, set MAVEN_HOME, or place mvn.cmd under C:\tools\apache-maven-3.9.9\bin\"
    exit 1
}

$env:Path = "$env:JAVA_HOME\bin;" + $env:Path

Write-Host "Using JAVA_HOME=$env:JAVA_HOME"
Write-Host "Using Maven=$mvn"
$mavenArgs = $args
if ($mavenArgs.Count -eq 0) {
    $mavenArgs = @("clean", "compile", "javafx:run")
}
& $mvn @mavenArgs
exit $LASTEXITCODE
