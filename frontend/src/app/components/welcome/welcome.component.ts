import { Component } from '@angular/core';

@Component({
  selector: 'app-welcome',
  templateUrl: './welcome.component.html',
  styleUrls: ['./welcome.component.scss']
})
export class WelcomeComponent {
  benefits = [
    'Metas de ahorro ilustradas en Tuticuenta',
    'Consejos semanales para padres y tutores',
    'Seguimiento de hábitos y recompensas divertidas'
  ];
}
