package xdi2.example.server.contributor.database;

import java.sql.SQLException;
import java.util.List;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.features.nodetypes.XdiEntity;
import xdi2.core.features.nodetypes.XdiEntityInstanceOrdered;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIArc;
import xdi2.core.syntax.XDIStatement;
import xdi2.core.util.XDIAddressUtil;
import xdi2.messaging.container.MessagingContainer;
import xdi2.messaging.container.contributor.ContributorMount;
import xdi2.messaging.container.contributor.ContributorResult;
import xdi2.messaging.container.contributor.impl.AbstractContributor;
import xdi2.messaging.container.exceptions.Xdi2MessagingException;
import xdi2.messaging.container.execution.ExecutionContext;
import xdi2.messaging.operations.GetOperation;
import xdi2.messaging.operations.SetOperation;

@ContributorMount(
		contributorXDIAddresses={"=user[#book]"}
		)
public class BooksConnector extends AbstractContributor {

	public BooksConnector() {

		super();

		this.getContributors().addContributor(new BookContributor());
	}

	/*
	 * Init and shutdown
	 */

	@Override
	public void init(MessagingContainer messagingContainer) throws Exception {

		super.init(messagingContainer);

		BookDao.init();
	}

	@Override
	public void shutdown(MessagingContainer container) throws Exception {

		super.shutdown(container);

		BookDao.shutdown();
	}

	@Override
	public ContributorResult executeGetOnAddress(XDIAddress[] contributorAddresses, XDIAddress contributorsAddress, XDIAddress relativeTargetAddress, GetOperation operation, Graph operationResultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

		// list books

		List<BookDao> books;

		try {

			books = BookDao.list();
		} catch (SQLException ex) {

			throw new Xdi2MessagingException("Database error: " + ex.getMessage(), ex, executionContext);
		}

		for (BookDao book : books) {

			XDIAddress XDIaddress = XDIAddressUtil.concatXDIAddresses(
					contributorsAddress, 
					XDIAddress.create("@" + book.getId()));

			ContextNode contextNode = operationResultGraph.setDeepContextNode(XDIaddress);

			bookToXDI(book, XdiEntityInstanceOrdered.fromContextNode(contextNode));
		}

		// done

		return ContributorResult.SKIP_MESSAGING_TARGET;
	}

	/*
	 * Sub-contributor
	 */

	@ContributorMount(
			contributorXDIAddresses={"{@0}"}
			)
	public class BookContributor extends AbstractContributor {

		public BookContributor() {

			super();

			this.getContributors().addContributor(new BookAttributeContributor());
		}
		
		@Override
		public ContributorResult executeGetOnAddress(XDIAddress[] contributorAddresses, XDIAddress contributorsAddress, XDIAddress relativeTargetAddress, GetOperation operation, Graph operationResultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

			if (contributorAddresses[1].equals(XDIAddress.create("{@0}"))) return ContributorResult.DEFAULT;

			// which ID

			long id = Integer.parseInt(contributorAddresses[1].toString().substring(1));

			// get book

			BookDao book;

			try {

				book = BookDao.get(id);
			} catch (SQLException ex) {

				throw new Xdi2MessagingException("Database error: " + ex.getMessage(), ex, executionContext);
			}

			if (book != null) {

				ContextNode contextNode = operationResultGraph.setDeepContextNode(contributorsAddress);

				bookToXDI(book, XdiEntityInstanceOrdered.fromContextNode(contextNode));
			}

			// done

			return ContributorResult.SKIP_MESSAGING_TARGET.or(ContributorResult.SKIP_PARENT_CONTRIBUTORS);
		}

		/*
		 * Sub-contributor
		 */

		@ContributorMount(
				contributorXDIAddresses={"{<#attr>}"}
				)
		public class BookAttributeContributor extends AbstractContributor {

			@Override
			public ContributorResult executeSetOnLiteralStatement(XDIAddress[] contributorAddresses, XDIAddress contributorsAddress, XDIStatement relativeTargetStatement, SetOperation operation, Graph operationResultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

				if (contributorAddresses[1].equals(XDIAddress.create("{@0}"))) return ContributorResult.DEFAULT;
				if (contributorAddresses[2].equals(XDIAddress.create("<#>"))) return ContributorResult.DEFAULT;

				// which ID

				long id = Integer.parseInt(contributorAddresses[1].toString().substring(1));

				// which attribute and value
				
				String attribute = contributorAddresses[2].getFirstXDIArc().getLiteral();
				Object value = relativeTargetStatement.getLiteralData();
				
				// set book attribute value

				try {

					BookDao.set(id, attribute, value);
				} catch (SQLException ex) {

					throw new Xdi2MessagingException("Database error: " + ex.getMessage(), ex, executionContext);
				}

				// done

				return ContributorResult.SKIP_MESSAGING_TARGET.or(ContributorResult.SKIP_PARENT_CONTRIBUTORS);
			}
		}
	}

	/*
	 * Helper methods
	 */

	private static void bookToXDI(BookDao book, XdiEntity xdiEntity) {

		xdiEntity.getXdiAttribute(XDIArc.create("<#title>"), true).setLiteralString(book.getTitle());
		xdiEntity.getXdiAttribute(XDIArc.create("<#author>"), true).setLiteralString(book.getAuthor());
		xdiEntity.getXdiAttribute(XDIArc.create("<#publisher>"), true).setLiteralString(book.getPublisher());
		xdiEntity.getXdiAttribute(XDIArc.create("<#country>"), true).setLiteralString(book.getCountry());
		xdiEntity.getXdiAttribute(XDIArc.create("<#year>"), true).setLiteralNumber(Double.valueOf(book.getYear()));
		xdiEntity.getXdiAttribute(XDIArc.create("<#price>"), true).setLiteralNumber(Double.valueOf(book.getPrice()));
	}
}
