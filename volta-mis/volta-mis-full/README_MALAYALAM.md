# VOLTA MIS — മലയാളം ഇൻസ്റ്റലേഷൻ ഗൈഡ്

## ഇതിൽ എന്തുണ്ട്?
- Login, Dashboard
- Customers, Projects, Staff
- Materials & Stock, Expenses
- Reports, Print report
- MySQL database (volta_mis)

## ആദ്യം ഒരിക്കൽ മാത്രം
1. Windows-ൽ Java 21, MySQL Server 8, Apache Maven 3.9+ ഇൻസ്റ്റാൾ ചെയ്തിരിക്കണം.
2. MySQL80 service പ്രവർത്തിക്കുന്നുണ്ടെന്ന് ഉറപ്പാക്കുക.
3. ഈ ഫോൾഡറിലെ `run.bat` ഫയലിൽ double-click ചെയ്യുക.
4. ചോദിക്കുമ്പോൾ MySQL root password നൽകുക. ടൈപ്പ് ചെയ്യുമ്പോൾ അക്ഷരങ്ങൾ കാണിക്കാതിരിക്കാം; അത് സാധാരണമാണ്.
5. തുടർന്ന് VOLTA MIS-നായി വേറൊരു login password സജ്ജമാക്കുക.
6. ആദ്യ തവണ Maven dependencies ഡൗൺലോഡ് ചെയ്യാൻ ഇന്റർനെറ്റ് ആവശ്യമാണ്. കറുത്ത വിൻഡോ അടയ്ക്കരുത്.
7. ബ്രൗസറിൽ `http://localhost:8080/login` തുറക്കുക.
8. Username `admin`; Password എന്നത് `run.bat`-ൽ നിങ്ങൾ നൽകിയതാണ്.

## ശ്രദ്ധിക്കേണ്ടത്
- ആദ്യ സ്റ്റാർട്ടപ്പിൽ `volta_mis` database സ്വയം ഉണ്ടാക്കാൻ ശ്രമിക്കും; schema.sql ആവശ്യമായ പട്ടികകൾ സൃഷ്ടിക്കും.
- ഓരോ തവണയും ഉപയോഗിക്കുമ്പോൾ `run.bat` പ്രവർത്തിപ്പിച്ച് തുറന്നിരിക്കുന്ന കറുത്ത വിൻഡോ അടയ്ക്കരുത്.
- സിസ്റ്റം പ്രവർത്തിക്കാൻ MySQL service പ്രവർത്തിക്കണം.
- `run.bat` വഴി നൽകിയ login password ആ റൺ സെഷനിൽ ഉപയോഗിക്കുന്നതാണ്. സ്ഥിരമായ password മാറ്റം/യഥാർത്ഥ ഉപയോക്തൃ അക്കൗണ്ടുകൾ ഈ പതിപ്പിൽ ഉൾപ്പെടുത്തിയിട്ടില്ല.
- ഈ പതിപ്പ് LAN/Internet-ലേക്ക് തുറന്ന് നൽകാൻ തയ്യാറാക്കിയതല്ല. ആദ്യം local computer-ൽ മാത്രം ഉപയോഗിക്കുക. യഥാർത്ഥ ബിസിനസ് ഡാറ്റയ്ക്കുമുമ്പ് backup, access control, security review എന്നിവ ക്രമീകരിക്കുക.
- Customer/Project/Staff/Materials/Expenses-ൽ നിലവിൽ add/list/delete ഉണ്ട്. Edit, PDF/Excel export, GST/invoice/accounting എന്നിവ ഇനിയും ഉൾപ്പെടുത്തിയിട്ടില്ല.

## പിശക് വന്നാൽ
കറുത്ത Command Prompt-ൽ അവസാനമായി കാണുന്ന error-ന്റെ screenshot അയയ്ക്കുക. `target` ഫോൾഡർ സ്വയം തുറക്കേണ്ടതില്ല.
