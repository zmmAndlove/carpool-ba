$headers = @{"Content-Type"="application/json"}
$body = '{"username": "testuser", "password": "123456"}'
$response = Invoke-WebRequest -Uri "http://localhost:8080/api/auth/login" -Method POST -Headers $headers -Body $body -UseBasicParsing
Write-Host $response.Content