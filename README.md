# Suraksha India — Android project

Native Android source project using Kotlin and Jetpack Compose.

## Included
- Offline safety information and locally saved trusted contacts
- SOS SMS workflow with optional location coordinates
- Emergency contact and helpline screens
- Flashlight/strobe utility (if supported by the phone)
- Simulated incoming-call screen (not a real police/emergency call)

## Build the APK using GitHub Actions
1. Create a GitHub repository.
2. Upload the **contents** of this folder to the repository root.
3. Open the repository's **Actions** tab and enable Actions if asked.
4. Select **Build Suraksha India APK** and click **Run workflow**.
5. After a successful run, open the run and download the artifact
   `Suraksha-India-Release-APK`. The APK is inside that downloaded ZIP.

The workflow installs Gradle 8.4 and JDK 17 directly. It does not rely on
Gradle wrapper scripts/JAR, which were missing from the original generated project.

## Important limitations before public release
- This project has not been compiled or tested on a physical phone in this environment.
- Android accepting an SMS request does not confirm delivery. Delivery depends on
  permissions, SIM, carrier, signal and device settings.
- Test SOS only with your own test number and consenting contacts. Do not repeatedly
  test by messaging emergency services.
- GPS can be unavailable indoors or when location permission/device settings are off.
- Verify all helpline numbers and legal guidance against current official Indian
  government sources before publishing. This is not an official government app.
- Before public distribution, test permissions and accessibility, prepare a privacy
  policy/data-safety disclosures, and configure a proper release signing key.

## App ID
`com.suraksha.india`
