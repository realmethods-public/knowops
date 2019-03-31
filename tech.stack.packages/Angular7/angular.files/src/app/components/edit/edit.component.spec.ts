#set( $className = $classObject.getName() )
#set( $lowercaseClassName = ${Utils.lowercaseFirstLetter(${className})} )
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { Edit${className}Component } from './edit.component';

describe('Edit${className}Component', () => {
  let component: Edit${className}Component;
  let fixture: ComponentFixture<Edit${className}Component>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [FormsModule],
      declarations: [ Edit${className}Component ]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(Edit${className}Component);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
