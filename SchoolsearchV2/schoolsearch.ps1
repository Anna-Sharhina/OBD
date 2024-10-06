javac src/Schoolsearch.java

if ($LASTEXITCODE -ne 0) {
    Write-Output "Error."
    exit 1
}

cd src/
java Schoolsearch
cd ..