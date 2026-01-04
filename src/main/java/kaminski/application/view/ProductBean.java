package kaminski.application.view;

import kaminski.application.model.Product;
import kaminski.application.service.ProductService;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.primefaces.PrimeFaces;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.List;

@Component
@ViewScoped
@RequiredArgsConstructor
public class ProductBean implements Serializable {

    private final ProductService productService;

    @Getter
    private List<Product> products;

    @Getter
    @Setter
    private Product selectedProduct;

    @Getter
    @Setter
    private List<Product> selectedProducts;

    @PostConstruct
    public void init() {
        this.products = productService.findAll();
    }

    public void openNew() {
        this.selectedProduct = new Product();
    }

    public void saveProduct() {
        if (this.selectedProduct.getId() == null) {
            this.products.add(this.selectedProduct);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Product Added"));
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Product Updated"));
        }

        productService.save(this.selectedProduct);
        PrimeFaces.current().executeScript("PF('manageProductDialog'.hide()");
        PrimeFaces.current().ajax().update("form:dt-products", "form:messages");

        this.products = productService.findAll();
    }

    public void deleteProduct() {
        productService.deleteById(this.selectedProduct.getId());
        this.products.remove(this.selectedProduct);
        this.selectedProduct = null;
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Product Removed"));
        PrimeFaces.current().ajax().update("form:dt-products", "form:messages");
    }

    public String getDeleteButtonMessage() {
        if (hasSelectedProducts()) {
            int size = this.selectedProducts.size();
            return size > 1 ? size + " products selected" : "1 product selected";
        }
        return "Delete";
    }

    public boolean hasSelectedProducts() {
        return this.selectedProducts != null && !this.selectedProducts.isEmpty();
    }

    public void deleteSelectedProducts() {
        this.selectedProducts.forEach(p -> productService.deleteById(p.getId()));
        this.products.removeAll(this.selectedProducts);
        this.selectedProducts = null;
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Products Removed"));
        PrimeFaces.current().ajax().update("form:dt-products", "form:messages");
    }
}
