package xdi2.example.server.contributor.database;

import java.sql.SQLException;
import java.util.List;

import xdi2.core.ContextNode;
import xdi2.core.features.nodetypes.XdiEntity;
import xdi2.core.features.nodetypes.XdiEntityMemberOrdered;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIArc;
import xdi2.core.util.XDIAddressUtil;
import xdi2.messaging.GetOperation;
import xdi2.messaging.MessageResult;
import xdi2.messaging.context.ExecutionContext;
import xdi2.messaging.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.MessagingTarget;
import xdi2.messaging.target.contributor.AbstractContributor;
import xdi2.messaging.target.contributor.ContributorMount;
import xdi2.messaging.target.contributor.ContributorResult;

@ContributorMount(
		contributorXDIAddresses={"[#book]"}
		)
public class BooksContributor extends AbstractContributor {

	public BooksContributor() {

		super();

		this.getContributors().addContributor(new BookContributor());
	}

	/*
	 * Init and shutdown
	 */

	@Override
	public void init(MessagingTarget messagingTarget) throws Exception {

		super.init(messagingTarget);

		BookDao.init();
	}

	@Override
	public void shutdown(MessagingTarget container) throws Exception {

		super.shutdown(container);

		BookDao.shutdown();
	}

	@Override
	public ContributorResult executeGetOnAddress(XDIAddress[] contributorAddresses, XDIAddress contributorsAddress, XDIAddress relativeTargetAddress, GetOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

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

			ContextNode contextNode = messageResult.getGraph().setDeepContextNode(XDIaddress);

			bookToXDI(book, XdiEntityMemberOrdered.fromContextNode(contextNode));
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

		@Override
		public ContributorResult executeGetOnAddress(XDIAddress[] contributorAddresses, XDIAddress contributorsAddress, XDIAddress relativeTargetAddress, GetOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

			if (contributorsAddress.equals(XDIAddress.create("[#book]{@0}"))) return ContributorResult.DEFAULT;

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

				ContextNode contextNode = messageResult.getGraph().setDeepContextNode(contributorsAddress);

				bookToXDI(book, XdiEntityMemberOrdered.fromContextNode(contextNode));
			}

			// done

			return ContributorResult.SKIP_MESSAGING_TARGET.or(ContributorResult.SKIP_PARENT_CONTRIBUTORS);
		}
	}

	/*
	 * Helper methods
	 */

	private static void bookToXDI(BookDao book, XdiEntity xdiEntity) {

		xdiEntity.getXdiAttribute(XDIArc.create("<#title>"), true).getXdiValue(true).setLiteralString(book.getTitle());
		xdiEntity.getXdiAttribute(XDIArc.create("<#author>"), true).getXdiValue(true).setLiteralString(book.getAuthor());
		xdiEntity.getXdiAttribute(XDIArc.create("<#publisher>"), true).getXdiValue(true).setLiteralString(book.getPublisher());
		xdiEntity.getXdiAttribute(XDIArc.create("<#country>"), true).getXdiValue(true).setLiteralString(book.getCountry());
		xdiEntity.getXdiAttribute(XDIArc.create("<#year>"), true).getXdiValue(true).setLiteralNumber(Double.valueOf(book.getYear()));
		xdiEntity.getXdiAttribute(XDIArc.create("<#price>"), true).getXdiValue(true).setLiteralNumber(Double.valueOf(book.getPrice()));
	}
}
