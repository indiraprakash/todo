if ([string]::IsNullOrEmpty($TodoServerAPIBaseUrl)) {
    $TodoServerAPIBaseUrlInput = Read-Host -Prompt "Specify the Base URI for the Todo REST API (default: 'http://localhost:8081/api/')"

    if ([string]::IsNullOrEmpty($TodoServerAPIBaseUrlInput)) {
        $TodoServerAPIBaseUrl = 'http://localhost:8081/api/';
    }
    else {
        $TodoServerAPIBaseUrl = $TodoServerAPIBaseUrlInput;
    }
}

$TodoItemsURL = "$($TodoServerAPIBaseUrl.TrimEnd("/"))/todos/"
$TodoUsersURL = "$($TodoServerAPIBaseUrl.TrimEnd("/"))/users/"
$TodoCategoriesURL = "$($TodoServerAPIBaseUrl.TrimEnd("/"))/categories/"

$TodoUserName = "TodoTestUser"
$TodoUserPassword = 'DoNotSetThisInClearText$$0012_1234'
$TodoUserCredential = New-Object -TypeName pscredential($TodoUserName, (ConvertTo-SecureString -String $TodoUserPassword -AsPlainText -Force))


$HttpGetDefaultParameters = @{
    Headers                        = @{"accept" = "application/json"}
    Method                         = "GET"
    AllowUnencryptedAuthentication = $true
    Credential                     = $TodoUserCredential
    Authentication                 = "Basic"
}

$HttpPostDefaultParameters = @{
    ContentType                    = "application/json"
    Method                         = "POST"
    AllowUnencryptedAuthentication = $true
    Credential                     = $TodoUserCredential
    Authentication                 = "Basic"
}

$HttpPutDefaultParameters = @{
    ContentType                    = "application/json"
    Method                         = "PUT"
    AllowUnencryptedAuthentication = $true
    Credential                     = $TodoUserCredential
    Authentication                 = "Basic"
}


$HttpDeleteDefaultParameters = @{
    Method                         = "DELETE"
    AllowUnencryptedAuthentication = $true
    Credential                     = $TodoUserCredential
    Authentication                 = "Basic"
}


Context "Todo REST API" {
    Describe "Todo User" {
        It "Login must Fail - 401 - no Credenitals" {
            try {
                $Response = Invoke-WebRequest -Uri $TodoItemsURL -Method Get -Headers @{"Accept" = "application/json"}
                $StatusCode = $Response.StatusCode
            }
            catch {
                Write-Verbose $_.Exception.Message
                $StatusCode = Select-String -InputObject $_.ErrorDetails -Pattern "HTTP Status (\d{3})" | Select-Object -ExpandProperty Matches | Select-Object -ExpandProperty Groups | Select-Object -Last 1 -ExpandProperty Value
            }

            $StatusCode | Should Be 401
        }

        It "Login must Fail - 401 - wrong Credentials" {
            try {
                $Response = Invoke-WebRequest -Uri $TodoItemsURL @HttpGetDefaultParameters
                $StatusCode = $Response.StatusCode
            }
            catch {
                Write-Verbose $_.Exception.Message
                $StatusCode = Select-String -InputObject $_.ErrorDetails -Pattern "HTTP Status (\d{3})" | Select-Object -ExpandProperty Matches | Select-Object -ExpandProperty Groups | Select-Object -Last 1 -ExpandProperty Value
            }

            $StatusCode | Should Be 401
        }

        It "Create User - Fails with 400 - invalid user data" {
            try {
                
                $Body = New-Object -TypeName psobject -Property @{
                    "name" = $TodoUserName
                }
                
                $Response = Invoke-WebRequest -Uri $TodoUsersURL -Body (ConvertTo-Json -InputObject $Body) @HttpPostDefaultParameters
                $StatusCode = $Response.StatusCode
            }
            catch {
                Write-Verbose $_.Exception.Message
                $StatusCode = Select-String -InputObject $_.ErrorDetails -Pattern "HTTP Status (\d{3})" | Select-Object -ExpandProperty Matches | Select-Object -ExpandProperty Groups | Select-Object -Last 1 -ExpandProperty Value
            }

            $StatusCode | Should Be 400
        }

        It "Create User" {
            try {
                
                $Body = New-Object -TypeName psobject -Property @{
                    "name"     = $TodoUserName
                    "password" = $TodoUserPassword
                }
                
                $Response = Invoke-WebRequest -Uri $TodoUsersURL -Body (ConvertTo-Json -InputObject $Body) @HttpPostDefaultParameters
                $StatusCode = $Response.StatusCode
            }
            catch {
                Write-Verbose $_.Exception.Message
                $StatusCode = Select-String -InputObject $_.ErrorDetails -Pattern "HTTP Status (\d{3})" | Select-Object -ExpandProperty Matches | Select-Object -ExpandProperty Groups | Select-Object -Last 1 -ExpandProperty Value
            }

            $StatusCode | Should Be 201
        }


        It "Create User - Fails with 409 - already exists" {
            try {
                
                $Body = New-Object -TypeName psobject -Property @{
                    "name"     = $TodoUserName
                    "password" = $TodoUserPassword
                }
                
                $Response = Invoke-WebRequest -Uri $TodoUsersURL -Body (ConvertTo-Json -InputObject $Body) @HttpPostDefaultParameters
                $StatusCode = $Response.StatusCode
            }
            catch {
                Write-Verbose $_.Exception.Message
                $StatusCode = Select-String -InputObject $_.ErrorDetails -Pattern "HTTP Status (\d{3})" | Select-Object -ExpandProperty Matches | Select-Object -ExpandProperty Groups | Select-Object -Last 1 -ExpandProperty Value
            }

            $StatusCode | Should Be 409
        }

        It "Create User - Fails with 415 - unsupported content type" {
            try {
                
                $Body = New-Object -TypeName psobject -Property @{
                    "name"     = $TodoUserName
                    "password" = $TodoUserPassword
                }
                
                $Response = Invoke-WebRequest -Uri $TodoUsersURL -Method Post -ContentType "application/xml" -Credential $TodoUserCredential -AllowUnencryptedAuthentication -Body (ConvertTo-Json -InputObject $Body)
                $StatusCode = $Response.StatusCode
            }
            catch {
                Write-Verbose $_.Exception.Message
                $StatusCode = Select-String -InputObject $_.ErrorDetails -Pattern "HTTP Status (\d{3})" | Select-Object -ExpandProperty Matches | Select-Object -ExpandProperty Groups | Select-Object -Last 1 -ExpandProperty Value
            }

            $StatusCode | Should Be 415
        }
        
    }

    Describe "Todos" {
        It "Get - Todos - Fails with 406 - unsupported accept type" {
            try {
                $Response = Invoke-WebRequest -Uri $TodoItemsURL -Credential $TodoUserCredential -AllowUnencryptedAuthentication -Authentication Basic
                $StatusCode = $Response.StatusCode
            }
            catch {
                Write-Verbose $_.Exception.Message
                $StatusCode = Select-String -InputObject $_.ErrorDetails -Pattern "HTTP Status (\d{3})" | Select-Object -ExpandProperty Matches | Select-Object -ExpandProperty Groups | Select-Object -Last 1 -ExpandProperty Value
            }

            $StatusCode | Should Be 406
        }


        It "Get - Todos - Fails with 401 - unauthorized" {
            try {
                $Response = Invoke-WebRequest -Uri $TodoItemsURL -AllowUnencryptedAuthentication -Headers @{"accept" = "application/json"}
                $StatusCode = $Response.StatusCode
            }
            catch {
                Write-Verbose $_.Exception.Message
                $StatusCode = Select-String -InputObject $_.ErrorDetails -Pattern "HTTP Status (\d{3})" | Select-Object -ExpandProperty Matches | Select-Object -ExpandProperty Groups | Select-Object -Last 1 -ExpandProperty Value
            }

            $StatusCode | Should Be 401
        }

        try {
            $Response = Invoke-WebRequest -Uri $TodoItemsURL @HttpGetDefaultParameters
            $StatusCode = $Response.StatusCode
            $Todos = ConvertFrom-Json -InputObject $Response.Content
            $InitialTodoCount = $Todos.Count
        }
        catch {
            Write-Verbose $_.Exception.Message
            $StatusCode = Select-String -InputObject $_.ErrorDetails -Pattern "HTTP Status (\d{3})" | Select-Object -ExpandProperty Matches | Select-Object -ExpandProperty Groups | Select-Object -Last 1 -ExpandProperty Value
        }

        It "Get - Todos" {
            $StatusCode | Should Be 200
        }
        

        # POST
        It "Create Todo - Fails with 400 - Invalid todo data" {
            try {
                
                $Body = New-Object -TypeName psobject -Property @{
                    "id" = 0
                }
                
                $Response = Invoke-WebRequest -Uri $TodoItemsURL -Body (ConvertTo-Json -InputObject $Body) @HttpPostDefaultParameters
                $StatusCode = $Response.StatusCode
            }
            catch {
                Write-Verbose $_.Exception.Message
                $StatusCode = Select-String -InputObject $_.ErrorDetails -Pattern "HTTP Status (\d{3})" | Select-Object -ExpandProperty Matches | Select-Object -ExpandProperty Groups | Select-Object -Last 1 -ExpandProperty Value
            }

            $StatusCode | Should Be 400	

        }

        It "Create Todo - Fails with 401 - unauthorized" {
            try {
                
                $Body = New-Object -TypeName psobject -Property @{
                    "id"        = 0
                    "title"     = "Get cofee"
                    "category"  = "Every day tasks"
                    "dueDate"   = "2019-01-15"
                    "important" = true
                    "completed" = false
                }
                
                $Response = Invoke-WebRequest -Uri $TodoItemsURL -Method POST -Body (ConvertTo-Json -InputObject $Body) -AllowUnencryptedAuthentication -ContentType "application/json" -Credential (New-Object -TypeName pscredential("FalseUser", (ConvertTo-SecureString -String "FalsePW" -AsPlainText -Force))) -Authentication Basic
                $StatusCode = $Response.StatusCode
            }
            catch {
                Write-Verbose $_.Exception.Message
                $StatusCode = Select-String -InputObject $_.ErrorDetails -Pattern "HTTP Status (\d{3})" | Select-Object -ExpandProperty Matches | Select-Object -ExpandProperty Groups | Select-Object -Last 1 -ExpandProperty Value
            }

            $StatusCode | Should Be 401	

        }

        It "Create Todo - Fails with 415 - unsupported content type" {
            try {
                
                $Body = New-Object -TypeName psobject -Property @{
                    "id"        = 0
                    "title"     = "Get cofee"
                    "category"  = "Every day tasks"
                    "dueDate"   = "2019-01-15"
                    "important" = $true
                    "completed" = $false
                }
                
                $Response = Invoke-WebRequest -Uri $TodoItemsURL -Method POST -Body (ConvertTo-Json -InputObject $Body) -AllowUnencryptedAuthentication -Credential $TodoUserCredential -Authentication Basic
                $StatusCode = $Response.StatusCode
            }
            catch {
                Write-Verbose $_.Exception.Message
                $StatusCode = Select-String -InputObject $_.ErrorDetails -Pattern "HTTP Status (\d{3})" | Select-Object -ExpandProperty Matches | Select-Object -ExpandProperty Groups | Select-Object -Last 1 -ExpandProperty Value
            }

            $StatusCode | Should Be 415	

        }

        $TodoList = @()
        $TestCategory = "Todo PS Testing $(Get-Date -Format 'yyyy-MM-dd_hh-mm-sss')"
        $TodoList += (New-Object -TypeName psobject -Property @{
                "title"     = "Get cofee"
                "category"  = $TestCategory
                "dueDate"   = "2019-01-15"
                "important" = $true
                "completed" = $false
            })

        $TodoList += (New-Object -TypeName psobject -Property @{
                "title"     = "Get beer"
                "category"  = "home"
                "dueDate"   = "2019-01-15"
                "important" = $true
                "completed" = $false
            })

        $TodoList += (New-Object -TypeName psobject -Property @{
                "title"     = "Get food"
                "category"  = $TestCategory
                "dueDate"   = "2019-01-15"
                "important" = $true
                "completed" = $false
            })

        $TodoList += (New-Object -TypeName psobject -Property @{
                "title"     = "Get a new laptop"
                "category"  = $TestCategory
                "dueDate"   = "2019-01-15"
                "important" = $true
                "completed" = $false
            })

        foreach ($Body in $TodoList) {
        
            try {                
                $Response = Invoke-WebRequest -Uri $TodoItemsURL -Body (ConvertTo-Json -InputObject $Body) @HttpPostDefaultParameters
                $StatusCode = $Response.StatusCode
            }
            catch {
                Write-Verbose $_.Exception.Message
                $StatusCode = Select-String -InputObject $_.ErrorDetails -Pattern "HTTP Status (\d{3})" | Select-Object -ExpandProperty Matches | Select-Object -ExpandProperty Groups | Select-Object -Last 1 -ExpandProperty Value
            }

            It "Create Todo '$($Body.title)' - success" {
                $StatusCode | Should Be 201	
            }
        
            $Id = ConvertFrom-Json -InputObject $Response.Content | Select-Object -ExpandProperty "Id"
            It "Create Todo - Return value is valid" {
                $Id | Should -BeGreaterThan 0
            }
        }

        try {
            $Response = Invoke-WebRequest -Uri "$($TodoItemsURL)$($id)" @HttpGetDefaultParameters
            $StatusCode = $Response.StatusCode
            $Todos = ConvertFrom-Json -InputObject $Response.Content
        }
        catch {
            Write-Verbose $_.Exception.Message
            $StatusCode = Select-String -InputObject $_.ErrorDetails -Pattern "HTTP Status (\d{3})" | Select-Object -ExpandProperty Matches | Select-Object -ExpandProperty Groups | Select-Object -Last 1 -ExpandProperty Value
        }

        It "Get created todo by Id" {
            $StatusCode | Should Be 200
        }

        $ReturnObject = ConvertFrom-Json -InputObject $Response.Content
        It "Get created todo by Id - Values are equal" {
            $null -eq (Compare-Object -ReferenceObject $Body -DifferenceObject $ReturnObject -Property "title", "category", "dueDate", "important", "completed") | Should Be $true
        }

        try {
            $Response = Invoke-WebRequest -Uri $TodoItemsURL @HttpGetDefaultParameters
            $StatusCode = $Response.StatusCode
            $Todos = ConvertFrom-Json -InputObject $Response.Content
        }
        catch {
            Write-Verbose $_.Exception.Message
            $StatusCode = Select-String -InputObject $_.ErrorDetails -Pattern "HTTP Status (\d{3})" | Select-Object -ExpandProperty Matches | Select-Object -ExpandProperty Groups | Select-Object -Last 1 -ExpandProperty Value
        }

        It "Get - Todos" {
            $StatusCode | Should Be 200
        }

        It "Todo - Count - One Todo Added!" {
            $Todos.Count | Should Be ($TodoList.Count + $InitialTodoCount)
        }

        # PUT
        $Body = New-Object -TypeName psobject -Property @{
            "id"        = $Id
            "title"     = "Get food"
            "category"  = "home"
            "dueDate"   = "2019-01-20"
            "important" = $false
            "completed" = $true
        }
        
        try {                
            $Response = Invoke-WebRequest -Uri "$($TodoItemsURL)$($id)" -Body (ConvertTo-Json -InputObject $Body) @HttpPutDefaultParameters
            $StatusCode = $Response.StatusCode
        }
        catch {
            Write-Verbose $_.Exception.Message
            $StatusCode = Select-String -InputObject $_.ErrorDetails -Pattern "HTTP Status (\d{3})" | Select-Object -ExpandProperty Matches | Select-Object -ExpandProperty Groups | Select-Object -Last 1 -ExpandProperty Value
        }

        It "Update Todo - success - 204" {
            $StatusCode | Should Be 204	
        }

        try {
            $Response = Invoke-WebRequest -Uri "$($TodoItemsURL)$($id)" @HttpGetDefaultParameters
            $StatusCode = $Response.StatusCode
            $Todos = ConvertFrom-Json -InputObject $Response.Content
        }
        catch {
            Write-Verbose $_.Exception.Message
            $StatusCode = Select-String -InputObject $_.ErrorDetails -Pattern "HTTP Status (\d{3})" | Select-Object -ExpandProperty Matches | Select-Object -ExpandProperty Groups | Select-Object -Last 1 -ExpandProperty Value
        }

        It "Get updated todo by Id" {
            $StatusCode | Should Be 200
        }

        $ReturnObject = ConvertFrom-Json -InputObject $Response.Content
        It "Get updated todo by Id - Values are equal" {
            $null -eq (Compare-Object -ReferenceObject $Body -DifferenceObject $ReturnObject -Property "title", "category", "dueDate", "important", "completed") | Should Be $true
        }

        # Categories
        try {
            $Response = Invoke-WebRequest -Uri "$($TodoItemsURL)?category=$($TestCategory)" @HttpGetDefaultParameters
            $StatusCode = $Response.StatusCode
            $TodosByCategory = ConvertFrom-Json -InputObject $Response.Content
        }
        catch {
            Write-Verbose $_.Exception.Message
            $StatusCode = Select-String -InputObject $_.ErrorDetails -Pattern "HTTP Status (\d{3})" | Select-Object -ExpandProperty Matches | Select-Object -ExpandProperty Groups | Select-Object -Last 1 -ExpandProperty Value
        }

        It "Get - Todos by Category" {
            $StatusCode | Should Be 200
        }

        It "Get Todos by Category - Number of Categories" {
            ($TodoList | Where-Object {$_.Category -eq $TestCategory}).count | Should Be (($TodosByCategory).Count + 1)
        }


        # Categories
        try {
            $Response = Invoke-WebRequest -Uri "$($TodoItemsURL)" @HttpGetDefaultParameters
            $StatusCode = $Response.StatusCode
            $Todos = ConvertFrom-Json -InputObject $Response.Content
        }
        catch {
            Write-Verbose $_.Exception.Message
            $StatusCode = Select-String -InputObject $_.ErrorDetails -Pattern "HTTP Status (\d{3})" | Select-Object -ExpandProperty Matches | Select-Object -ExpandProperty Groups | Select-Object -Last 1 -ExpandProperty Value
        }

        It "Get - All Todos" {
            $StatusCode | Should Be 200
        }

        try {
            $Response = Invoke-WebRequest -Uri "$($TodoCategoriesURL)" @HttpGetDefaultParameters
            $StatusCode = $Response.StatusCode
            $Categories = ConvertFrom-Json -InputObject $Response.Content
        }
        catch {
            Write-Verbose $_.Exception.Message
            $StatusCode = Select-String -InputObject $_.ErrorDetails -Pattern "HTTP Status (\d{3})" | Select-Object -ExpandProperty Matches | Select-Object -ExpandProperty Groups | Select-Object -Last 1 -ExpandProperty Value
        }

        It "Get - All Categories" {
            $StatusCode | Should Be 200
        }

        It "Get - Categories Count" {
            $Categories.count | Should Be ($Todos | Select-Object -ExpandProperty Category -Unique).count
        }

        foreach ($CategoryToTest in ($Todos | Select-Object -ExpandProperty Category -Unique)) {
            It "Category available - '$($CategoryToTest)'" {
                $Categories -contains $CategoryToTest | Should Be $true
            }
        }

        foreach ($Todo in $Todos) {
            try {
                $Response = Invoke-WebRequest -Uri "$($TodoItemsURL)$($Todo.id)" @HttpDeleteDefaultParameters
                $StatusCode = $Response.StatusCode
            }
            catch {
                Write-Verbose $_.Exception.Message
                $StatusCode = Select-String -InputObject $_.ErrorDetails -Pattern "HTTP Status (\d{3})" | Select-Object -ExpandProperty Matches | Select-Object -ExpandProperty Groups | Select-Object -Last 1 -ExpandProperty Value
            }
    
            It "Delete - Todo with Title '$($Todo.Title)'" {
                $StatusCode | Should Be 204
            }
        }

        try {
            $Response = Invoke-WebRequest -Uri "$($TodoItemsURL)$($Todo.id)" @HttpDeleteDefaultParameters
            $StatusCode = $Response.StatusCode
            $Categories = ConvertFrom-Json -InputObject $Response.Content
        }
        catch {
            Write-Verbose $_.Exception.Message
            $StatusCode = Select-String -InputObject $_.ErrorDetails -Pattern "HTTP Status (\d{3})" | Select-Object -ExpandProperty Matches | Select-Object -ExpandProperty Groups | Select-Object -Last 1 -ExpandProperty Value
        }

        It "Delete - Fails with 404 - Todo not found" {
            $StatusCode | Should Be 404
        }
    }
}