### Tabel: Logare (Autentificare)

| Câmp | Detalii |
| :--- | :--- |
| **Nume** | Logare (Autentificare) |
| **Actori participanți** | User |
| **Scenariu normal** | 1. Utilizatorul alege opțiunea de intrare în cont (Login). <br> &nbsp;&nbsp;&nbsp;&nbsp; 2. Sistemul afișează un formular și solicită e-mailul și parola. <br> 3. Utilizatorul introduce datele și confirmă. <br> &nbsp;&nbsp;&nbsp;&nbsp; 4. Sistemul confirmă autentificarea și oferă acces la funcționalitățile de cont (ex: "Comenzile mele"). |
| **Scenarii de excepție** | **Scenariu alternativ 1:** datele de conectare sunt incorecte. <br> 4a. Sistemul afișează mesajul: "E-mail sau parolă greșită". <br> &nbsp;&nbsp;&nbsp;&nbsp; 4a.1 Utilizatorul reintroduce datele și se revine la punctul 4. <br><br> **Scenariu alternativ 2:** utilizatorul confirmă fără să completeze toate câmpurile obligatorii. <br> 4b. Sistemul blochează trimiterea și indică vizual eroarea ("Câmp obligatoriu"). <br> &nbsp;&nbsp;&nbsp;&nbsp; 4b.1 Utilizatorul completează datele și se revine la punctul 4. |

<br>

### Tabel: Căutare și Filtrare Cărți în pagina principală

| Câmp | Detalii |
| :--- | :--- |
| **Nume** | Căutare și Filtrare Cărți în pagina principală |
| **Actori participanți** | User (Client) |
| **Scenariu normal** | 1. Utilizatorul accesează pagina principală unde este afișată lista tuturor cărților. <br> 2. Utilizatorul introduce un cuvânt cheie (titlu) în bara de căutare. <br> &nbsp;&nbsp;&nbsp;&nbsp; 3. Sistemul filtrează în timp real sau la apăsarea tastei Enter și afișează doar cărțile care conțin textul respectiv. <br> 4. Utilizatorul apasă butonul de filtrare. <br> &nbsp;&nbsp;&nbsp;&nbsp; 5. Sistemul afișează o listă cu toate genurile literare disponibile. <br> 6. Utilizatorul selectează un gen literar din listă. <br> &nbsp;&nbsp;&nbsp;&nbsp; 7. Sistemul actualizează afișarea, păstrând doar cărțile care aparțin genului selectat. |
| **Scenarii de excepție** | **Scenariu alternativ 1:** sistemul caută textul introdus în bază și nu găsește nicio potrivire. <br> 3a. Sistemul afișează mesajul: "Ne pare rău, nu am găsit nicio carte pentru '[titlu_căutat]'." <br> &nbsp;&nbsp;&nbsp;&nbsp; 3a.1 Utilizatorul apasă butonul "x" pentru a reveni la lista completă. <br><br> **Scenariu alternativ 2:** utilizatorul selectează un gen care momentan nu are nicio carte asociată în stoc. <br> 7a. Sistemul afișează: "Momentan nu avem cărți disponibile pentru acest gen." <br> &nbsp;&nbsp;&nbsp;&nbsp; 7a.1 Se revine la afișarea generală după închiderea filtrului. |



<br>

### Tabel: Gestionare Coș de Cumpărături

| Câmp | Detalii |
| :--- | :--- |
| **Nume** | Gestionare Coș de Cumpărături |
| **Actori** | Client |
| **Scenariu normal** | 1. Clientul selectează opțiunea "Adaugă în coș" pentru o carte. <br> &nbsp;&nbsp;&nbsp;&nbsp; 2. Sistemul actualizează cantitatea și prețul total al coșului. <br> 3. Clientul solicită vizualizarea conținutului coșului. <br> &nbsp;&nbsp;&nbsp;&nbsp; 4. Sistemul afișează lista cărților, cantitatea pentru fiecare și prețul total. <br> 5. Clientul modifică numărul de exemplare pentru o carte sau șterge un titlu din listă. <br> &nbsp;&nbsp;&nbsp;&nbsp; 6. Sistemul recalculează totalul și afișează coșul actualizat. |
| **Scenarii alternative** | 5a. Clientul șterge toate produsele. <br> &nbsp;&nbsp;&nbsp;&nbsp; 5a.1 Sistemul afișează "Coșul este gol". |


<br>

### Tabel: Plasare Comandă

| Câmp | Detalii |
| :--- | :--- |
| **Nume** | Plasare Comandă |
| **Actori** | Client, Sistem Gestiune Stoc |
| **Scenariu normal** | 1. Clientul alege finalizarea comenzii din coș. <br> &nbsp;&nbsp;&nbsp;&nbsp; 2. Sistemul verifică disponibilitatea în tabela "Carte" și confirmă stocul. <br> &nbsp;&nbsp;&nbsp;&nbsp; 3. Sistemul solicită detaliile de livrare prin afișarea unui formular. <br> 4. Clientul completează detaliile și confirmă comanda. <br> &nbsp;&nbsp;&nbsp;&nbsp; 5. Sistemul scade cantitatea din stoc și înregistrează o nouă "Tranzactie". <br> &nbsp;&nbsp;&nbsp;&nbsp; 6. Sistemul confirmă succesul operațiunii. |
| **Scenarii alternative** | 2a. **Lipsă stoc:** Sistemul afișează mesajul "Produsul nu mai este în stoc" și blochează finalizarea. |

<br>

### Tabel: Vizualizare Istoric Comenzi

| Câmp | Detalii |
| :--- | :--- |
| **Nume** | Vizualizare Istoric Comenzi |
| **Actori participanți** | Client (Inițiator) |
| **Flux de evenimente (MSS)** | 1. Clientul accesează secțiunea „Contul meu”. <br> &nbsp;&nbsp;&nbsp;&nbsp; 2. Sistemul afișează o listă cronologică cu detaliile fiecărei comenzi (data, titlurile cărților, prețul total, statusul). |
| **Scenarii alternative** | Nu există nicio tranzacție salvată pentru acest utilizator. <br> 2a. Sistemul afișează mesajul: „Încă nu ai efectuat nicio comandă. Te invităm să răsfoiești catalogul nostru!”. |
