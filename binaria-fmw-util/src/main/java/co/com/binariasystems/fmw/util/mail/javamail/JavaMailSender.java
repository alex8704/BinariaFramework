package co.com.binariasystems.fmw.util.mail.javamail;

import java.io.InputStream;

import javax.mail.internet.MimeMessage;

import co.com.binariasystems.fmw.util.mail.MailException;
import co.com.binariasystems.fmw.util.mail.MailSender;

public interface JavaMailSender extends MailSender {
	/**
	 * Create a new JavaMail MimeMessage for the underlying JavaMail Session
	 * of this sender. Needs to be called to create MimeMessage instances
	 * that can be prepared by the client and passed to send(MimeMessage).
	 * @return the new MimeMessage instance
	 * @see #send(MimeMessage)
	 * @see #send(MimeMessage[])
	 */
	MimeMessage createMimeMessage();

	/**
	 * Create a new JavaMail MimeMessage for the underlying JavaMail Session
	 * of this sender, using the given input stream as the message source.
	 * @param contentStream the raw MIME input stream for the message
	 * @return the new MimeMessage instance
	 * @throws org.springframework.mail.MailParseException
	 * in case of message creation failure
	*/
	MimeMessage createMimeMessage(InputStream contentStream) throws MailException;

	/**
	 * Send the given JavaMail MIME message.
	 * The message needs to have been created with {@link #createMimeMessage()}.
	 * @param mimeMessage message to send
	 * @throws org.springframework.mail.MailAuthenticationException
	 * in case of authentication failure
	 * @throws org.springframework.mail.MailSendException
	 * in case of failure when sending the message
	 * @see #createMimeMessage
	 */
	void send(MimeMessage mimeMessage) throws MailException;

	/**
	 * Send the given array of JavaMail MIME messages in batch.
	 * The messages need to have been created with {@link #createMimeMessage()}.
	 * @param mimeMessages messages to send
	 * @throws org.springframework.mail.MailAuthenticationException
	 * in case of authentication failure
	 * @throws org.springframework.mail.MailSendException
	 * in case of failure when sending a message
	 * @see #createMimeMessage
	 */
	void send(MimeMessage[] mimeMessages) throws MailException;

	/**
	 * Send the JavaMail MIME message prepared by the given MimeMessagePreparator.
	 * <p>Alternative way to prepare MimeMessage instances, instead of
	 * {@link #createMimeMessage()} and {@link #send(MimeMessage)} calls.
	 * Takes care of proper exception conversion.
	 * @param mimeMessagePreparator the preparator to use
	 * @throws org.springframework.mail.MailPreparationException
	 * in case of failure when preparing the message
	 * @throws org.springframework.mail.MailParseException
	 * in case of failure when parsing the message
	 * @throws org.springframework.mail.MailAuthenticationException
	 * in case of authentication failure
	 * @throws org.springframework.mail.MailSendException
	 * in case of failure when sending the message
	 */
	void send(MimeMessagePreparator mimeMessagePreparator) throws MailException;

	/**
	 * Send the JavaMail MIME messages prepared by the given MimeMessagePreparators.
	 * <p>Alternative way to prepare MimeMessage instances, instead of
	 * {@link #createMimeMessage()} and {@link #send(MimeMessage[])} calls.
	 * Takes care of proper exception conversion.
	 * @param mimeMessagePreparators the preparator to use
	 * @throws org.springframework.mail.MailPreparationException
	 * in case of failure when preparing a message
	 * @throws org.springframework.mail.MailParseException
	 * in case of failure when parsing a message
	 * @throws org.springframework.mail.MailAuthenticationException
	 * in case of authentication failure
	 * @throws org.springframework.mail.MailSendException
	 * in case of failure when sending a message
	 */
	void send(MimeMessagePreparator[] mimeMessagePreparators) throws MailException;
}
