# Import schema.sql and seed-demo.sql using the same credentials as the Java app.
# Requires local-db-env.ps1 (copy from local-db-env.ps1.example).
# Do not paste extra text on the same line as mysql -p — use this script instead.
$ErrorActionPreference = "Stop"
Set-Location $PSScriptRoot

$localDbEnv = Join-Path $PSScriptRoot "local-db-env.ps1"
if (-not (Test-Path $localDbEnv)) {
    Write-Error "Missing local-db-env.ps1. Copy local-db-env.ps1.example to local-db-env.ps1 and set your MySQL user/password."
    exit 1
}
. $localDbEnv

$user = $env:CIVICTRACK_DB_USERNAME
$pass = $env:CIVICTRACK_DB_PASSWORD
if ([string]::IsNullOrWhiteSpace($user) -or $null -eq $pass -or $pass -eq "") {
    Write-Error "Set CIVICTRACK_DB_USERNAME and CIVICTRACK_DB_PASSWORD in local-db-env.ps1"
    exit 1
}

$mysql = $env:CIVICTRACK_MYSQL_EXE
if ([string]::IsNullOrWhiteSpace($mysql)) {
    $mysql = "C:\Program Files\MySQL\MySQL Server 9.5\bin\mysql.exe"
}
if (-not (Test-Path $mysql)) {
    Write-Error "mysql.exe not found at '$mysql'. Install MySQL or set environment variable CIVICTRACK_MYSQL_EXE to the full path of mysql.exe"
    exit 1
}

Write-Host "Using mysql.exe: $mysql"
Write-Host "Importing schema.sql..."
Get-Content (Join-Path $PSScriptRoot "schema.sql") -Raw | & $mysql -u $user -p"$pass"

Write-Host "Importing seed-demo.sql..."
Get-Content (Join-Path $PSScriptRoot "seed-demo.sql") -Raw | & $mysql -u $user -p"$pass"

Write-Host "Tables in civictrack:"
& $mysql -u $user -p"$pass" -e "USE civictrack; SHOW TABLES;"
Write-Host "Done."
