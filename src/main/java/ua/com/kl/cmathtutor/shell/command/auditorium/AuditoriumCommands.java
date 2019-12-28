package ua.com.kl.cmathtutor.shell.command.auditorium;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliAvailabilityIndicator;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.shell.support.util.OsUtils;
import org.springframework.stereotype.Component;

import ua.com.kl.cmathtutor.domain.entity.Auditorium;
import ua.com.kl.cmathtutor.service.AuditoriumService;
import ua.com.kl.cmathtutor.shell.command.ExceptionWrapperUtils;

@Component
public class AuditoriumCommands implements CommandMarker {

    @Autowired
    private AuditoriumService auditoriumService;

    @CliAvailabilityIndicator({ "auditorium all", "auditorium by-name" })
    public boolean isAuditoriumCommandsAvailable() {
	return true;
    }

    @CliCommand(value = "auditorium all", help = "View list of all available auditoriums in the theater")
    public String getAllAuditoriums() {
	return "There are following auditoriums in the theater:" + OsUtils.LINE_SEPARATOR +
		auditoriumService.getAll().stream().map(Auditorium::getName)
			.map(str -> str + OsUtils.LINE_SEPARATOR).collect(StringBuilder::new,
				StringBuilder::append, StringBuilder::append);
    }

    @CliCommand(value = "auditorium by-name", help = "View auditorium by its name")
    public String getAuditoriumByName(@CliOption(key = {
	    "name" }, mandatory = true, help = "Auditorium name to fetch the auditorium data") final String name) {
	return ExceptionWrapperUtils.handleException(() -> auditoriumService.getByName(name).toString());
    }
}
