package xdi2.example.server.contributor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.exceptions.Xdi2RuntimeException;
import xdi2.core.impl.AbstractLiteralNode;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIStatement;
import xdi2.core.util.XDIAddressUtil;
import xdi2.messaging.GetOperation;
import xdi2.messaging.MessageResult;
import xdi2.messaging.SetOperation;
import xdi2.messaging.context.ExecutionContext;
import xdi2.messaging.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.MessagingTarget;
import xdi2.messaging.target.Prototype;
import xdi2.messaging.target.contributor.AbstractContributor;
import xdi2.messaging.target.contributor.ContributorResult;

public class FastCollectionContributor extends AbstractContributor implements Prototype<FastCollectionContributor> {

	private static final Logger log = LoggerFactory.getLogger(FastCollectionContributor.class);

	private String path;

	public FastCollectionContributor(String path) {

		super();

		this.path = path;
		new File(this.path).mkdir();
	}

	/*
	 * Prototype
	 */

	@Override
	public FastCollectionContributor instanceFor(PrototypingContext prototypingContext) throws Xdi2MessagingException {

		// done

		return this;
	}

	/*
	 * Init and shutdown
	 */

	@Override
	public void init(MessagingTarget messagingTarget) throws Exception {

		super.init(messagingTarget);
	}

	@Override
	public ContributorResult executeGetOnAddress(XDIAddress[] contributorAddresses, XDIAddress contributorsAddress, XDIAddress relativeTargetAddress, GetOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		Object literalData;

		final String filename = filename(contributorsAddress);

		// list files

		File[] files = new File(this.path).listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {

				return name.startsWith(filename);
			}
		});

		// read files

		for (File file : files) {

			try {

				BufferedReader reader = new BufferedReader(new FileReader(file));
				String line = reader.readLine();
				literalData = AbstractLiteralNode.stringToLiteralData(line);
				reader.close();
			} catch (IOException ex) {

				log.warn("Error reading file: " + ex.getMessage(), ex);
				literalData = null;
			}

			// result

			if (literalData != null) {

				messageResult.getGraph().setDeepContextNode(address(file.getName())).setLiteralNode(literalData);
			}
		}

		// done

		return ContributorResult.SKIP_MESSAGING_TARGET;
	}

	@Override
	public ContributorResult executeSetOnLiteralStatement(XDIAddress[] contributorAddresses, XDIAddress contributorsAddress, XDIStatement relativeTargetStatement, SetOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		String filename = filename(XDIAddressUtil.concatXDIAddresses(contributorsAddress, relativeTargetStatement.getSubject()));

		// write file

		try {

			BufferedWriter writer = new BufferedWriter(new FileWriter(new File(this.path, filename)));
			writer.write(AbstractLiteralNode.literalDataToString(relativeTargetStatement.getLiteralData()));
			writer.close();
		} catch (IOException ex) {

			throw new Xdi2MessagingException("Error writing file: " + ex.getMessage(), ex, executionContext);
		}

		// done

		return ContributorResult.SKIP_MESSAGING_TARGET;
	}

	/*
	 * Helper methods
	 */

	private static String filename(XDIAddress XDIaddress) {

		try {

			return URLEncoder.encode(XDIaddress.toString(), "UTF-8");
		} catch (UnsupportedEncodingException ex) {

			throw new Xdi2RuntimeException(ex.getMessage(), ex);
		}
	}
	
	private static XDIAddress address(String filename) {
		
		try {

			return XDIAddress.create(URLDecoder.decode(filename, "UTF-8"));
		} catch (UnsupportedEncodingException ex) {

			throw new Xdi2RuntimeException(ex.getMessage(), ex);
		}
	}
}
