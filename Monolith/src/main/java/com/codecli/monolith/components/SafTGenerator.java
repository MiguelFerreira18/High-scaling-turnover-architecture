package com.codecli.monolith.components;

import com.codecli.monolith.Models.CompanyInfo;
import com.codecli.monolith.Models.Invoice;
import com.codecli.monolith.Models.Product;
import com.codecli.monolith.Models.User;
import com.codecli.monolith.repo.CompanyRepo;
import com.codecli.monolith.repo.InvoiceRepo;
import com.codecli.monolith.repo.ProductRepo;
import com.codecli.monolith.repo.UserRepo;
import jakarta.xml.bind.DatatypeConverter;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.crypto.dsig.DigestMethod;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.security.MessageDigest;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

@Component
public class SafTGenerator {
    private final CompanyRepo companyRepo;
    private final InvoiceRepo invoiceRepo;
    private final ProductRepo productRepo;

    public SafTGenerator(CompanyRepo companyRepo, InvoiceRepo invoiceRepo, ProductRepo productRepo) {
        this.companyRepo = companyRepo;
        this.invoiceRepo = invoiceRepo;
        this.productRepo = productRepo;
    }
    /*
    //More or les the structure of saf-t in Portugal
    - AuditFiles
        -Header
        -MasterFiles
            - GeneralLedgerAccounts
            - Customer
            - Supplier
            - Product
            - TaxTable
        -GeneralLedgerEntrie
        -SOurceDocuments
            - salesInvoices
            - PurchaseInvoices
            - Payments
            -
     */

    public void createSafT() throws Exception {
        Iterable<Product> products = productRepo.findAll();
        List<Float> ivas = productRepo.findAllIvas();
        List<Invoice> invoices = StreamSupport.stream(invoiceRepo.findAll().spliterator(), false).toList();

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();

        Document document = builder.newDocument();
        Element root = document.createElement("AuditFiles");
        root.setAttribute("xmlns", "urn:OECD:StandardAuditFile-Tax:PT_1.04_01");
        root.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
        root.setAttribute("xsi:schemaLocation", "urn:OECD:StandardAuditFile-Tax:PT_1.04_01\n" +
                "                               SAFTPT1.04_01.xsd");
        document.appendChild(root);
        root.appendChild(createHeader(document));
        root.appendChild(createMasterFiles(document, products, ivas));
        root.appendChild(createGeneralLedgerEntry(document, invoices));
        root.appendChild(createSourceDocument(document, invoices));

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(document);

        StreamResult result = new StreamResult("./output.xml");
        transformer.transform(source, result);

        System.out.println("XML file created successfully");
    }

    private Element createHeader(Document document) {
        //Inits
        Queue<Element> queue = new LinkedList<>();
        CompanyInfo info = StreamSupport.stream(companyRepo.findAll().spliterator(), false)
                .toList().get(0);
        Element header = document.createElement("Header");
        String version = "1.04_01";
        String startDate = LocalDate.now().getYear() + "-01-01";
        String endDate = LocalDate.now().getYear() + "-12-31";
        String currency = "EUR";
        String dateCreated = LocalDate.now().toString();
        String taxEntity = "Global";
        String productCompanyTax = "999999990";

        queue.add(createTextElements(document, "AuditFileVersion", version));
        queue.add(createTextElements(document, "CompanyID", String.valueOf(info.getNif())));
        queue.add(createTextElements(document, "TaxRegistrationNumber", String.valueOf(info.getNif())));
        queue.add(createTextElements(document, "TaxAccountingBasis", "C"));
        queue.add(createTextElements(document, "CompanyName", info.getCompanyName()));
        queue.add(createTextElements(document, "BusinessName", info.getName()));
        queue.add(createTextElements(document, "CompanyAddress", info.getTaxAddress()));
        queue.add(createTextElements(document, "FiscalYear", String.valueOf(LocalDate.now().getYear())));
        queue.add(createTextElements(document, "StartDate", startDate));
        queue.add(createTextElements(document, "EndDate", endDate));
        queue.add(createTextElements(document, "CurrencyCode", currency));
        queue.add(createTextElements(document, "DateCreated", dateCreated));
        queue.add(createTextElements(document, "TaxEntity", taxEntity));
        queue.add(createTextElements(document, "ProductCompanyTax", productCompanyTax));
        queue.add(createTextElements(document, "SoftwareCertificateNumber", "9999"));
        queue.forEach(header::appendChild);

        //INFO: There are other headers like ProductId,ProductVersion,Telephone,Fax etc. These are extra so i won't be adding them.

        return header;
    }

    private Element createMasterFiles(Document document, Iterable<Product> products, List<Float> ivas) {
        Element element = document.createElement("MasterFiles");
        createCustomer(document).forEach(element::appendChild);
        createProducts(document, products).forEach(element::appendChild);
        element.appendChild(createTaxTable(document, ivas));

        return element;
    }

    private List<Element> createCustomer(Document document) {
        return invoiceRepo.findAllUsersWithAPurchase().stream()
                .map(c -> createCustomer(document, c)).toList();
    }

    //In theory these are customers that made a purchase, that is what i believe
    private Element createCustomer(Document document, User user) {
        Queue<Element> queue = new LinkedList<>();
        Element element = document.createElement("Customer");

        queue.add(createTextElements(document, "CustomerID", user.getId()));
        queue.add(createTextElements(document, "AccountID", user.getId() + "-ID"));
        queue.add(createTextElements(document, "CompanyName", user.getUsername()));
        queue.add(createTextElements(document, "CustomerTaxID", String.valueOf(user.getNif())));
        queue.add(createTextElements(document, "Email", user.getEmail()));

        return element;
    }

    private List<Element> createProducts(Document document, Iterable<Product> products) {
        return StreamSupport.stream(products.spliterator(), false)
                .map(p -> createProduct(document, p)).toList();
    }

    private Element createProduct(Document document, Product product) {
        Queue<Element> queue = new LinkedList<>();
        Element element = document.createElement("Product");

        String productType = "P";
        String productCode = "PROD-" + product.getID();
        String productGroup = "Mercadorias";

        queue.add(createTextElements(document, "ProductType", productType));
        queue.add(createTextElements(document, "ProductCode", productCode));
        queue.add(createTextElements(document, "ProductGroup", productGroup));
        queue.add(createTextElements(document, "ProductDescription", product.getName()));
        queue.add(createTextElements(document, "ProductNumberCode", product.getSku())); //Ideally this is generated with a specific algorithm
        queue.forEach(element::appendChild);

        return element;
    }

    private Element createTaxTable(Document document, List<Float> ivas) {
        Element element = document.createElement("TaxTable");
        createTaxTableEntries(document, ivas).forEach(element::appendChild);
        return element;
    }

    private List<Element> createTaxTableEntries(Document document, List<Float> ivas) {
        return ivas.stream()
                .map(i -> createTaxTableEntry(document, i))
                .toList();
    }

    private Element createTaxTableEntry(Document document, float iva) {
        Queue<Element> queue = new LinkedList<>();
        Element element = document.createElement("TaxTableEntry");

        String taxType = "IVA";
        String taxCountryRegion = "PT";
        String taxCode = "NOR";
        String taxExpirationDate = "9999-12-31";
        String taxPercentage = String.valueOf(iva * 100);

        queue.add(createTextElements(document, "TaxType", taxType));
        queue.add(createTextElements(document, "TaxCountryRegion", taxCountryRegion));
        queue.add(createTextElements(document, "TaxCode", taxCode)); //To simplify it's going to be normal with random values
        queue.add(createTextElements(document, "TaxExpirationDate", taxExpirationDate));
        queue.add(createTextElements(document, "TaxPercentage", taxPercentage));
        queue.forEach(element::appendChild);

        return element;
    }

    private Element createGeneralLedgerEntry(Document document, List<Invoice> invoices) {
        Queue<Element> queue = new LinkedList<>();
        Element element = document.createElement("GeneralLedgerEntries");
        float debit = invoices.stream().map(Invoice::getTotalAfterTax).reduce(Float::sum).get();

        queue.add(createTextElements(document, "NumberOfEntries", "1"));
        queue.add(createTextElements(document, "TotalDebit", String.valueOf(debit)));
        queue.add(createTextElements(document, "TotalCredit", String.valueOf(debit))); //Let's use the same just to be easier
        queue.add(createJournal(document, invoices));
        queue.forEach(element::appendChild);

        return element;
    }

    private Element createJournal(Document document, List<Invoice> invoices) {
        Queue<Element> queue = new LinkedList<>();
        Element element = document.createElement("Journal");

        queue.add(createTextElements(document, "JournalID", "VD"));
        queue.add(createTextElements(document, "Description", "Sales journal"));
        queue.add(createTextElements(document, "JournalID", "VD"));
        queue.add(createTransaction(document, invoices));
        queue.forEach(element::appendChild);

        return element;
    }

    private Element createTransaction(Document document, List<Invoice> invoices) {
        Element element = document.createElement("Transaction");
        createTransactionLines(document, invoices).forEach(element::appendChild);
        return element;
    }

    private List<Element> createTransactionLines(Document document, List<Invoice> invoices) {
        float totalWithIva = invoices.stream()
                .map(Invoice::getTotalAfterTax)
                .reduce(Float::sum)
                .get();
        float totalWithoutIva = invoices.stream()
                .map(Invoice::getTotal)
                .reduce(Float::sum)
                .get();
        float totalIvaLiquidated = invoices.stream()
                .map(Invoice::getTotalLiquidatedIva)
                .reduce(Float::sum)
                .get();
        List<Element> elements = List.of(
                createTransactionLine(document, "1", "21111", "Invoice client - total value with Iva", String.valueOf(totalWithIva), true),
                createTransactionLine(document, "2", "71111", "Product Sales", String.valueOf(totalWithoutIva), false),
                createTransactionLine(document, "3", "24321", "Liquidated Iva", String.valueOf(totalIvaLiquidated), false) // For realistic purposes you should have the different tiers
        );

        return elements;
    }

    private Element createTransactionLine(Document document, String recordID, String accountID, String description, String amount, boolean isDebit) {
        Queue<Element> queue = new LinkedList<>();
        Element element = document.createElement("Line");

        queue.add(createTextElements(document, "RecordID", recordID));
        queue.add(createTextElements(document, "AccountID", accountID));
        queue.add(createTextElements(document, "Description", description));
        if (isDebit) {
            queue.add(createTextElements(document, "DebitAmount", amount));
        } else {
            queue.add(createTextElements(document, "CreditAmount", amount));
        }
        queue.forEach(element::appendChild);

        return element;
    }

    /*
    For the invoices there is a distinction between Payments and Invoice, the booleans is there,
    but to make this less boring i will only do for the Sales Invoices
    The other 2 are exactly the same and coded almost the same way, but
    they require different parameters and I don't want to be bothered by that.
     */
    private Element createSourceDocument(Document document, List<Invoice> invoices) {
        Element element = document.createElement("SourceDocument");
        element.appendChild(createSalesInvoices(document, invoices));
        return element;
    }

    private Element createSalesInvoices(Document document, List<Invoice> invoices) {
        Element element = document.createElement("SalesInvoices");
        createInvoices(document, invoices).forEach(element::appendChild);
        return element;
    }

    private List<Element> createInvoices(Document document, List<Invoice> invoices) {
        return invoices.stream()
                .map(i -> createInvoice(document, i))
                .toList();
    }

    private Element createInvoice(Document document, Invoice invoice) {
        Queue<Element> queue = new LinkedList<>();
        Element element = document.createElement("Invoice");
        String hash = "";
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update((invoice.getATCUD() + invoice.getUser().getUsername()).getBytes());
            hash = DatatypeConverter.printHexBinary(md.digest());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        queue.add(createTextElements(document, "InvoiceNo", invoice.getID()));
        queue.add(createTextElements(document, "ATCUD", invoice.getATCUD()));
        queue.add(createTextElements(document, "Hash", hash));
        queue.add(createTextElements(document, "SourceID", invoice.getUser().getId()));
        queue.addAll(createInvoiceLines(document, invoice.getProducts()));
        queue.add(createInvoiceDocumentTotals(document, invoice));
        queue.forEach(element::appendChild);

        return element;
    }

    private List<Element> createInvoiceLines(Document document, List<Product> products) {
        return IntStream.range(0, products.size())
                .mapToObj(i -> createInvoiceLine(document, products.get(i), i))
                .toList();
    }

    private Element createInvoiceLine(Document document, Product product, int i) {
        Queue<Element> queue = new LinkedList<>();
        Element element = document.createElement("Line");
        String lineNumber = String.valueOf(i + 1);
        String productCode = "PROD-" + i;
        String unitOfMeasure = "UN";

        //There are many other fields and the Tax field is simplified
        queue.add(createTextElements(document, "LineNumber", lineNumber));
        queue.add(createTextElements(document, "ProductCode", productCode));
        queue.add(createTextElements(document, "UnitOfMeasure", unitOfMeasure));
        queue.add(createTextElements(document, "UnitPrice", String.valueOf(product.getPrice())));
        queue.add(createTextElements(document, "Tax", String.valueOf(product.getIva() * 100)));
        queue.forEach(element::appendChild);

        return element;
    }

    private Element createInvoiceDocumentTotals(Document document, Invoice invoice) {
        Queue<Element> queue = new LinkedList<>();
        Element element = document.createElement("DocumentTotals");

        queue.add(createTextElements(document, "TaxPayable", String.valueOf(invoice.getTotalLiquidatedIva())));
        queue.add(createTextElements(document, "NetTotal", String.valueOf(invoice.getTotal())));
        queue.add(createTextElements(document, "GrossTotal", String.valueOf(invoice.getTotalAfterTax())));
        queue.add(createTextElements(document, "CurrencyCode", "EUR")); // This would be another element with multiple values, but i want to simplify
        queue.forEach(element::appendChild);

        return element;
    }

    private Element createTextElements(Document document, String elementName, String value) {
        Element element = document.createElement(elementName);
        element.appendChild(document.createTextNode(value));
        return element;
    }

}
