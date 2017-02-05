package xdi2.example.server.contributor.fastcollection;

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

import xdi2.core.Graph;
import xdi2.core.exceptions.Xdi2RuntimeException;
import xdi2.core.impl.AbstractLiteralNode;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIStatement;
import xdi2.core.util.XDIAddressUtil;
import xdi2.messaging.operations.GetOperation;
import xdi2.messaging.operations.SetOperation;
import xdi2.messaging.container.MessagingContainer;
import xdi2.messaging.container.contributor.ContributorResult;
import xdi2.messaging.container.contributor.impl.AbstractContributor;
import xdi2.messaging.container.exceptions.Xdi2MessagingException;
import xdi2.messaging.container.execution.ExecutionContext;

public class FastCollectionContributor extends AbstractContributor {

	private static final Logger log = LoggerFactory.getLogger(FastCollectionContributor.class);

	private String path;

	public FastCollectionContributor(String path) {

		super();

		this.path = path;
		new File(this.path).mkdir();
	}

	/*
	 * Init and shutdown
	 */

	@Override
	public void init(MessagingContainer messagingContainer) throws Exception {

		super.init(messagingContainer);
	}

	@Override
	public ContributorResult executeGetOnAddress(XDIAddress[] contributorAddresses, XDIAddress contributorsAddress, XDIAddress relativeTargetAddress, GetOperation operation, Graph operationResultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

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

				operationResultGraph.setDeepContextNode(address(file.getName())).setLiteralNode(literalData);
			}
		}

		// done

		return ContributorResult.SKIP_MESSAGING_TARGET;
	}

	@Override
	public ContributorResult executeSetOnLiteralStatement(XDIAddress[] contributorAddresses, XDIAddress contributorsAddress, XDIStatement relativeTargetStatement, SetOperation operation, Graph operationResultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

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
