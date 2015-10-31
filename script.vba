Sub LeturiLookup()

    'Get company from user
    Dim companyNameInput As Variant
    companyNameInput = InputBox("Enter the company.", "Company Name Request")
    If companyNameInput = "" Then
        Exit Sub
    End If

    'Get columns from user
    Dim numberColumnInput As Variant
    Dim numberColumn As Long
    numberColumnInput = InputBox("Enter the column with product numbers.", "Product Number Column")
    numberColumn = Cells(1, numberColumnInput).Column

    Dim codeColumnInput As Variant
    Dim codeColumn As Long
    codeColumnInput = InputBox("Enter the column for IB codes.", "IB Code Column")
    codeColumn = Cells(1, codeColumnInput).Column

    'Look up file variables
    Dim lwb As Workbook
    Dim lws As Worksheet
    Dim lrng As Range
    Dim lcell As Range

    'Working sheet variables
    Dim ci As Long

    'Call Code Map workbook
    Set lwb = Workbooks.Open("C:\Users\schelluri\Desktop\AUTOMATIC CATEGORIZATION\MAPS.xlsx")
    lwb.Activate
    On Error Resume Next
    Set lws = Sheets(companyNameInput)
    If Err.Number = 9 Then
        lookWorkbook.Close
        MsgBox "The requested company does not have data in the mapping workbook." & vbNewLine & vbNewLine & "No changes were made.", 0, "Company Not Found"
        Exit Sub
    End If

    'Find last in map
    Dim mapLast As Long
    mapLast = lws.Cells(1, 1).End(xlDown).Row
    Set lrng = lws.Range("A2:A" & mapLast)

    'Find last in price list
    Dim listLast As Long
    listLast = Cells(1, 1).End(xlDown).Row

    'Search and conditioning
    For ci = 2 To listLast 'Product number range
        If Not IsNull(Cells(ci, numberColumn).Value) Then
            For Each lcell In lrng.Cells
                If Cells(ci, numberColumn).Value Like lcell.Value & "*" Then
                    Cells(ci, codeColumn).Value = lrng.Cells(lcell.Row - 1, lcell.Column + 1).Value
                End If
            Next lcell
        End If
    Next ci

End Sub
