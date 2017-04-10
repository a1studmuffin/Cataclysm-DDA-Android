import subprocess, sys, os

 # Store arguments, skipping the script name
args = sys.argv[1:]

# Check for debug/release parameter, displaying usage if neither was specified
is_release = False
if 'debug' in args:
    is_release = False
elif 'release' in args:
    is_release = True
else:
    print '''
    This script will build, deploy, launch and log output from the game on an Android device.

    If the environment variable ANDROID_DEVICE is set, it will attempt to connect to this device via adb.

    Usage: ''' + os.path.split(sys.argv[0])[1] + ''' (clean) debug|release

    clean       Clean build. Removes intermediate files from ndk-build & ant.
    debug       Debug build profile. Contains debugging symbol information.
    release     Release build profile. Use this for distribution builds.'''
    exit(1)

# Check for optional clean parameter
clean = False
if 'clean' in args:
    clean = True

# Function to run a command and optionally exit the script on failure
def run(command, exit_on_failure = True):
    print '=' * 78
    print 'Running command: ' + command
    print '=' * 78
    print ' '
    return_code = subprocess.call(command, shell=True)
    print ' '
    if return_code != 0 and exit_on_failure:
        print '=== Command failed: ' + command
        print '=== Return code: ' + str(return_code)
        print '=' * 78
        exit(return_code)

# Attempt connection to Android device if specified in environment variables, eg. 'mydevice:5555'
if 'ANDROID_DEVICE' in os.environ:
    run('adb connect ' + os.environ['ANDROID_DEVICE'], False)
else:
    print '=' * 78
    print 'ANDROID_DEVICE environment variable not set, assuming existing adb connection...'
    print '=' * 78
    print ' '

# Clean ndk-build output, with some extra manual cleanup as ndk-build doesn't fully cleanup properly
if clean:
    run('ndk-build clean')
    run('rmdir /S /Q obj', False) # TODO: Remove Windows-specific rmdir. shutil.rmtree is somewhat unreliable under Windows.
    run('rmdir /S /Q libs', False)

# Run ndk-build with appropriate parameters for debug/release
run('ndk-build -j2 NDK_DEBUG=' + ('0 APP_CFLAGS+=-DRELEASE=1' if is_release else '1'))

# Clean ant build
if clean:
    run('ant clean')

# Run ant build with appropriate parameters for debug/release and install the app on device
run('ant ' + ('release' if is_release else 'debug') + ' install')

# Launch the app once installed
run('adb shell monkey -p com.MichaelDavies.CataclysmDDA -c android.intent.category.LAUNCHER 1')

# Run logcat with appropriate filters so we can see TTY output from the app
run('adb logcat -s "Splash","cdda","CDDA","SDL","SDL_android"')

print '=' * 78
print 'Done.'
