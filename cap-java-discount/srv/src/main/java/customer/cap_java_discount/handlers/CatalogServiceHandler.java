package customer.cap_java_discount.handlers;

import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sap.cds.ql.Update;
import com.sap.cds.ql.cqn.CqnUpdate;
import com.sap.cds.services.cds.CdsService;
import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.After;
import com.sap.cds.services.handler.annotations.On;
import com.sap.cds.services.handler.annotations.ServiceName;
import com.sap.cds.services.persistence.PersistenceService;

import cds.gen.catalogservice.Books;
import cds.gen.catalogservice.CatalogService_;
import cds.gen.catalogservice.DecreaseStockContext;

@Component
@ServiceName(CatalogService_.CDS_NAME)
public class CatalogServiceHandler implements EventHandler {

	@Autowired
	private PersistenceService persistenceService;

	@After(event = CdsService.EVENT_READ)
	public void discountBooks(Stream<Books> books) {
		books.filter(b -> b.getTitle() != null && b.getStock() != null)
				.filter(b -> b.getStock() >= 500)
				.forEach(b -> b.setTitle(b.getTitle() + " (discounted)"));
	}

	@On(event = DecreaseStockContext.CDS_NAME)
	public void decreaseStock(DecreaseStockContext eventContext) {
		Books book = persistenceService.run(eventContext.getCqn()).single(Books.class);

		book.setStock(book.getStock() - 1);

		CqnUpdate updateStatement = Update.entity(eventContext.getTarget()).data(book);
		persistenceService.run(updateStatement);

		eventContext.setCompleted();
	}

}
