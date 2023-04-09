from kivymd.app import MDApp
from kivy.lang.builder import Builder
from kivy.uix.screenmanager import Screen, ScreenManager
from Screens import screens
from Themes import colors
from kivy.core.window import Window
from kivy.clock import Clock
from CalculatorFunctions import SimpleCalculator
from MathFunctions import APlusB

Window.size = (360, 640)


class LoadingScreen(Screen): pass


class MainScreen(Screen): pass


class Maths(Screen): pass


class Physics(Screen): pass


class Binary(Screen): pass


class CalKivy(MDApp):
    def __init__(self):
        super().__init__()
        self.SCR_Manager = None
        self.screen = None

    def build(self):
        # Loading strings from builder
        self.screen = Builder.load_string(screens)
        self.SCR_Manager = ScreenManager()

        # Theme and colors
        self.theme_cls.colors = colors
        self.theme_cls.primary_palette = "Red"
        self.theme_cls.theme_style = 'Light'
        self.SCR_Manager.add_widget(MainScreen(name='LoadingScreen'))

        return self.screen

    def on_start(self):
        Clock.schedule_once(self.change_s,5)

    def change_s(self, dt):
        self.screen.current = 'CALKIVY'


if __name__ == '__main__':
    CalKivy().run()
