package dev.ridill.mym.settings.domain.back_up

import com.google.android.gms.common.Scopes
import com.google.android.gms.common.api.Scope

class GDriveService {
    companion object {
        val driveScopesString: List<String>
            get() = listOf(
                Scopes.DRIVE_FULL
            )

        val driveScopes: List<Scope>
            get() = driveScopesString.map { Scope(it) }
    }


}